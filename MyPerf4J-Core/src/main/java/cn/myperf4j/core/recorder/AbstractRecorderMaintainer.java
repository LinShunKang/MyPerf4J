package cn.myperf4j.core.recorder;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.metric.processor.MethodMetricsProcessor;
import cn.myperf4j.base.util.ExecutorManager;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.ThreadUtils;
import cn.myperf4j.base.Scheduler;
import cn.myperf4j.core.MethodMetricsCalculator;
import cn.myperf4j.core.MethodTagMaintainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/4/25
 */
public abstract class AbstractRecorderMaintainer implements Scheduler {

    protected List<Recorders> recordersList;

    protected MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private int curIndex = 0;

    private volatile Recorders curRecorders;

    private ThreadPoolExecutor backgroundExecutor;

    private MethodMetricsProcessor methodMetricsProcessor;

    private boolean accurateMode;

    public boolean initial(MethodMetricsProcessor processor, boolean accurateMode, int backupRecordersCount) {
        this.methodMetricsProcessor = processor;
        this.accurateMode = accurateMode;
        backupRecordersCount = getFitBackupRecordersCount(backupRecordersCount);

        if (!initRecorders(backupRecordersCount)) {
            return false;
        }

        if (!initBackgroundTask(backupRecordersCount)) {
            return false;
        }

        return initOther();
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

    private boolean initBackgroundTask(int backupRecordersCount) {
        try {
            backgroundExecutor = new ThreadPoolExecutor(1,
                    2,
                    1,
                    TimeUnit.MINUTES,
                    new LinkedBlockingQueue<Runnable>(backupRecordersCount),
                    ThreadUtils.newThreadFactory("MyPerf4J-BackgroundExecutor_"),
                    new ThreadPoolExecutor.DiscardOldestPolicy());

            ExecutorManager.addExecutorService(backgroundExecutor);
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

    public abstract void addRecorder(int methodTagId, ProfilingParams params);

    public Recorder getRecorder(int methodTagId) {
        return curRecorders.getRecorder(methodTagId);
    }

    @Override
    public void run(long lastTimeSliceStartTime, long millTimeSlice) {
        try {
            final Recorders tmpCurRecorders = curRecorders;
            tmpCurRecorders.setStartTime(lastTimeSliceStartTime);
            tmpCurRecorders.setStopTime(lastTimeSliceStartTime + millTimeSlice);

            curIndex = getNextIdx(curIndex);
            Logger.debug("RecorderMaintainer.roundRobinProcessor curIndex=" + curIndex % recordersList.size());

            Recorders nextRecorders = recordersList.get(curIndex % recordersList.size());
            nextRecorders.setStartTime(lastTimeSliceStartTime + millTimeSlice);
            nextRecorders.setStopTime(lastTimeSliceStartTime + 2 * millTimeSlice);
            nextRecorders.setWriting(true);
            nextRecorders.resetRecorder();
            curRecorders = nextRecorders;

            tmpCurRecorders.setWriting(false);
            backgroundExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (tmpCurRecorders.isWriting()) {
                        Logger.warn("RecorderMaintainer.backgroundExecutor the tmpCurRecorders is writing!!! Please increase `MillTimeSlice` or increase `RecorderTurntableNum`!!!P1");
                        return;
                    }

                    long start = System.currentTimeMillis();
                    try {
                        methodMetricsProcessor.beforeProcess(tmpCurRecorders.getStartTime(), tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                        int actualSize = methodTagMaintainer.getMethodTagCount();
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
                            MethodMetrics metrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                            methodMetricsProcessor.process(metrics, tmpCurRecorders.getStartTime(), tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                        }
                    } catch (Exception e) {
                        Logger.error("RecorderMaintainer.backgroundExecutor error", e);
                    } finally {
                        methodMetricsProcessor.afterProcess(tmpCurRecorders.getStartTime(), tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                        Logger.debug("RecorderMaintainer.backgroundProcessor finished!!! cost: " + (System.currentTimeMillis() - start) + "ms");
                    }
                }
            });
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.roundRobinExecutor error", e);
        }
    }

    private int getNextIdx(int idx) {
        if (idx == Integer.MAX_VALUE) {
            return 0;
        }
        return idx + 1;
    }

}
