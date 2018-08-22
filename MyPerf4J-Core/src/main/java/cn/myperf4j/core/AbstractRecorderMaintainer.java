package cn.myperf4j.core;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.MethodMetricsProcessor;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/4/25
 */
public abstract class AbstractRecorderMaintainer {

    protected List<Recorders> recordersList;

    protected MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private int curIndex = 0;

    private volatile Recorders curRecorders;

    private ThreadPoolExecutor backgroundExecutor;

    private MethodMetricsProcessor methodMetricsProcessor;

    private boolean accurateMode;

    private long millTimeSlice;

    private volatile long nextTimeSliceEndTime = 0L;

    public boolean initial(MethodMetricsProcessor processor, boolean accurateMode, int backupRecordersCount, long millTimeSlice) {
        this.methodMetricsProcessor = processor;
        this.accurateMode = accurateMode;
        this.millTimeSlice = getFitMillTimeSlice(millTimeSlice);
        backupRecordersCount = getFitBackupRecordersCount(backupRecordersCount);

        if (!initRecorders(backupRecordersCount)) {
            return false;
        }

        if (!initRoundRobinTask()) {
            return false;
        }

        if (!initBackgroundTask(backupRecordersCount)) {
            return false;
        }

        return initOther();
    }

    private long getFitMillTimeSlice(long millTimeSlice) {
        if (millTimeSlice < PropertyValues.MIN_TIME_SLICE) {
            return PropertyValues.MIN_TIME_SLICE;
        } else if (millTimeSlice > PropertyValues.MAX_TIME_SLICE) {
            return PropertyValues.MAX_TIME_SLICE;
        }
        return millTimeSlice;
    }

    private int getFitBackupRecordersCount(int backupRecordersCount) {
        if (backupRecordersCount < PropertyValues.MIN_BACKUP_RECORDERS_COUNT) {
            return PropertyValues.MIN_BACKUP_RECORDERS_COUNT;
        } else if (backupRecordersCount > PropertyValues.MAX_BACKUP_RECORDERS_COUNT) {
            return PropertyValues.MAX_BACKUP_RECORDERS_COUNT;
        }
        return backupRecordersCount;
    }

    private boolean initRecorders(int backupRecordersCount) {
        recordersList = new ArrayList<>(backupRecordersCount + 1);
        for (int i = 0; i < backupRecordersCount + 1; ++i) {
            Recorders recorders = new Recorders(new AtomicReferenceArray<Recorder>(MethodTagMaintainer.MAX_NUM));
            recordersList.add(recorders);
        }

        curRecorders = recordersList.get(curIndex % recordersList.size());
        return true;
    }

    private boolean initRoundRobinTask() {
        try {
            ScheduledThreadPoolExecutor roundRobinExecutor = new ScheduledThreadPoolExecutor(1,
                    ThreadUtils.newThreadFactory("MyPerf4J-RoundRobinExecutor_"),
                    new ThreadPoolExecutor.DiscardPolicy());

            roundRobinExecutor.scheduleAtFixedRate(new RoundRobinProcessor(), 0, 10, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initRoundRobinTask()", e);
        }
        return false;
    }

    private boolean initBackgroundTask(int backupRecordersCount) {
        try {
            backgroundExecutor = new ThreadPoolExecutor(1,
                    2,
                    1,
                    TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(backupRecordersCount),
                    ThreadUtils.newThreadFactory("MyPerf4J-BackgroundExecutor_"),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initBackgroundTask()", e);
        }
        return false;
    }

    public abstract boolean initOther();

    protected Recorder createRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        if (accurateMode) {
            return AccurateRecorder.getInstance(methodTagId, mostTimeThreshold, outThresholdCount);
        }
        return RoughRecorder.getInstance(methodTagId, mostTimeThreshold);
    }

    public Recorders getRecorders() {
        return curRecorders;
    }

    public abstract void addRecorder(int methodTagId, ProfilingParams params);

    public Recorder getRecorder(int methodTagId) {
        return curRecorders.getRecorder(methodTagId);
    }

    private class RoundRobinProcessor implements Runnable {

        @Override
        public void run() {
            long currentMills = System.currentTimeMillis();
            if (nextTimeSliceEndTime == 0L) {
                nextTimeSliceEndTime = ((currentMills / millTimeSlice) * millTimeSlice) + millTimeSlice;
            }

            //还在当前的时间片里
            if (nextTimeSliceEndTime > currentMills) {
                return;
            }
            nextTimeSliceEndTime = ((currentMills / millTimeSlice) * millTimeSlice) + millTimeSlice;

            try {
                final Recorders tmpCurRecorders = curRecorders;
                tmpCurRecorders.setStartTime(nextTimeSliceEndTime - 2 * millTimeSlice);
                tmpCurRecorders.setStopTime(nextTimeSliceEndTime - millTimeSlice);

                curIndex = getNextIdx(curIndex);
                Logger.debug("RecorderMaintainer.roundRobinProcessor curIndex=" + curIndex % recordersList.size());

                Recorders nextRecorders = recordersList.get(curIndex % recordersList.size());
                nextRecorders.setStartTime(nextTimeSliceEndTime - millTimeSlice);
                nextRecorders.setStopTime(nextTimeSliceEndTime);
                nextRecorders.setWriting(true);
                nextRecorders.resetRecorder();
                curRecorders = nextRecorders;

                tmpCurRecorders.setWriting(false);
                backgroundExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        long start = System.currentTimeMillis();
                        try {
                            if (tmpCurRecorders.isWriting()) {
                                Logger.warn("RecorderMaintainer.backgroundExecutor the tmpCurRecorders is writing!!! Please increase `MillTimeSlice` or increase `RecorderTurntableNum`!!!P1");
                                return;
                            }

                            int actualSize = methodTagMaintainer.getMethodTagCount();
                            List<MethodMetrics> methodMetricsList = new ArrayList<>(actualSize / 2);
                            for (int i = 0; i < actualSize; ++i) {
                                Recorder recorder = tmpCurRecorders.getRecorder(i);
                                if (recorder == null || !recorder.hasRecord()) {
                                    continue;
                                }

                                if (tmpCurRecorders.isWriting()) {
                                    Logger.warn("RecorderMaintainer.backgroundExecutor the tmpCurRecorders is writing!!! Please increase `MillTimeSlice` or increase `RecorderTurntableNum`!!!P2");
                                    break;
                                }

                                MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
                                methodMetricsList.add(PerfStatsCalculator.calPerfStats(recorder, methodTag, tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime()));
                            }

                            methodMetricsProcessor.process(methodMetricsList, actualSize, tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                        } catch (Exception e) {
                            Logger.error("RecorderMaintainer.backgroundExecutor error", e);
                        } finally {
                            Logger.debug("RecorderMaintainer.backgroundProcessor finished!!! cost: " + (System.currentTimeMillis() - start) + "ms");
                        }
                    }
                });
            } catch (Exception e) {
                Logger.error("RecorderMaintainer.roundRobinExecutor error", e);
            } finally {
                Logger.debug("RecorderMaintainer.roundRobinProcessor finished!!! cost: " + (System.currentTimeMillis() - currentMills) + "ms");
            }
        }
    }

    private int getNextIdx(int idx) {
        if (idx == Integer.MAX_VALUE) {
            return 0;
        }
        return idx + 1;
    }

}
