package cn.myperf4j.core.recorder;

import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyKeys.Metrics;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.metric.exporter.MethodMetricsExporter;
import cn.myperf4j.base.util.ExecutorManager;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.ThreadUtils;
import cn.myperf4j.base.Scheduler;
import cn.myperf4j.core.MethodMetricsCalculator;
import cn.myperf4j.core.MethodMetricsHistogram;
import cn.myperf4j.core.MethodTagMaintainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/4/25
 */
public abstract class AbstractRecorderMaintainer implements Scheduler {

    private volatile boolean initialState;

    protected List<Recorders> recordersList;

    private final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private int curIndex;

    private volatile Recorders curRecorders;

    private ThreadPoolExecutor backgroundExecutor;

    private MethodMetricsExporter methodMetricsExporter;

    private boolean accurateMode;

    public boolean initial(MethodMetricsExporter processor, boolean accurateMode, int bakRecordersCnt) {
        this.methodMetricsExporter = processor;
        this.accurateMode = accurateMode;
        bakRecordersCnt = getFitBakRecordersCnt(bakRecordersCnt);

        if (!initRecorders(bakRecordersCnt)) {
            return false;
        }

        if (!initBackgroundTask(bakRecordersCnt)) {
            return false;
        }

        return initialState = initOther();
    }

    private int getFitBakRecordersCnt(int backupRecordersCount) {
        if (backupRecordersCount < PropertyValues.Recorder.MIN_BACKUP_RECORDERS_COUNT) {
            return PropertyValues.Recorder.MIN_BACKUP_RECORDERS_COUNT;
        } else if (backupRecordersCount > PropertyValues.Recorder.MAX_BACKUP_RECORDERS_COUNT) {
            return PropertyValues.Recorder.MAX_BACKUP_RECORDERS_COUNT;
        }
        return backupRecordersCount;
    }

    private boolean initRecorders(int backupRecordersCount) {
        recordersList = new ArrayList<>(backupRecordersCount + 1);
        for (int i = 0; i < backupRecordersCount + 1; ++i) {
            Recorders recorders = new Recorders(new AtomicReferenceArray<Recorder>(MethodTagMaintainer.MAX_NUM));
            recordersList.add(recorders);
        }

        curRecorders = recordersList.get(curIndex);
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
            if (!initialState) {
                Logger.warn("AbstractRecorderMaintainer.run(long, long): initialState is false!");
                return;
            }

            final Recorders tmpCurRecorders = curRecorders;
            tmpCurRecorders.setStartTime(lastTimeSliceStartTime);
            tmpCurRecorders.setStopTime(lastTimeSliceStartTime + millTimeSlice);

            curIndex = getNextIdx(curIndex, recordersList.size());
            Logger.debug("RecorderMaintainer.roundRobinProcessor curIndex=" + curIndex);

            Recorders nextRecorders = recordersList.get(curIndex);
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
                        Logger.warn("RecorderMaintainer.backgroundExecutor the tmpCurRecorders is writing!!! " +
                                "Please increase `" + Metrics.TIME_SLICE_METHOD +
                                "` or increase `" + PropertyKeys.Recorder.BACKUP_COUNT + "`!!!P1");
                        return;
                    }

                    long start = System.currentTimeMillis();
                    try {
                        methodMetricsExporter.beforeProcess(tmpCurRecorders.getStartTime(),
                                tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                        int actualSize = methodTagMaintainer.getMethodTagCount();
                        for (int i = 0; i < actualSize; ++i) {
                            if (tmpCurRecorders.isWriting()) {
                                Logger.warn("RecorderMaintainer.backgroundExecutor the tmpCurRecorders is " +
                                        "writing!!! Please increase `" + Metrics.TIME_SLICE_METHOD +
                                        "` or increase `" + PropertyKeys.Recorder.BACKUP_COUNT + "`!!!P2");
                                break;
                            }

                            Recorder recorder = tmpCurRecorders.getRecorder(i);
                            if (recorder == null) {
                                continue;
                            } else if (!recorder.hasRecord()) {
                                MethodMetricsHistogram.recordNoneMetrics(recorder.getMethodTagId());
                                continue;
                            }

                            MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
                            MethodMetrics metrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag,
                                    tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                            MethodMetricsHistogram.recordMetrics(metrics);
                            methodMetricsExporter.process(metrics, tmpCurRecorders.getStartTime(),
                                    tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                        }
                    } catch (Throwable e) {
                        Logger.error("RecorderMaintainer.backgroundExecutor error", e);
                    } finally {
                        methodMetricsExporter.afterProcess(tmpCurRecorders.getStartTime(),
                                tmpCurRecorders.getStartTime(), tmpCurRecorders.getStopTime());
                        Logger.debug("RecorderMaintainer.backgroundProcessor finished!!! cost: " +
                                (System.currentTimeMillis() - start) + "ms");
                    }
                }
            });
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.roundRobinExecutor error", e);
        }
    }

    private int getNextIdx(int idx, int maxIdx) {
        return (idx + 1) % maxIdx;
    }

    @Override
    public String name() {
        return "RecorderMaintainer";
    }
}
