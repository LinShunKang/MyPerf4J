package cn.myperf4j.core.recorder;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.Scheduler;
import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.exporter.MethodMetricsExporter;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.concurrent.ExecutorManager;
import cn.myperf4j.core.MethodMetricsHistogram;
import cn.myperf4j.core.MethodTagMaintainer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static cn.myperf4j.base.constant.PropertyKeys.Metrics.TIME_SLICE_METHOD;
import static cn.myperf4j.base.constant.PropertyKeys.Recorder.BACKUP_COUNT;
import static cn.myperf4j.base.constant.PropertyValues.Recorder.MAX_BACKUP_RECORDERS_COUNT;
import static cn.myperf4j.base.constant.PropertyValues.Recorder.MIN_BACKUP_RECORDERS_COUNT;
import static cn.myperf4j.base.util.concurrent.ThreadUtils.newThreadFactory;
import static cn.myperf4j.core.MethodMetricsCalculator.calMetrics;
import static cn.myperf4j.core.MethodMetricsHistogram.recordNoneMetrics;
import static cn.myperf4j.core.MethodTagMaintainer.MAX_NUM;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by LinShunkang on 2018/4/25
 */
public abstract class AbstractRecorderMaintainer implements Scheduler {

    private static final int MAX_DELAY_TASKS = 1024;

    private volatile boolean initialState;

    protected List<Recorders> recordersList;

    private int curIndex;

    private volatile Recorders curRecorders;

    private ScheduledThreadPoolExecutor backgroundExecutor;

    private MethodMetricsExporter methodMetricsExporter;

    public boolean initial(MethodMetricsExporter processor, int bakRecordersCnt) {
        this.methodMetricsExporter = processor;
        if (!initRecorders(getFitBakRecordersCnt(bakRecordersCnt)) || !initBackgroundTask()) {
            return false;
        }
        return initialState = initOther();
    }

    private int getFitBakRecordersCnt(int backupRecordersCount) {
        if (backupRecordersCount < MIN_BACKUP_RECORDERS_COUNT) {
            return MIN_BACKUP_RECORDERS_COUNT;
        } else if (backupRecordersCount > MAX_BACKUP_RECORDERS_COUNT) {
            return MAX_BACKUP_RECORDERS_COUNT;
        }
        return backupRecordersCount;
    }

    private boolean initRecorders(int backupRecordersCount) {
        recordersList = new ArrayList<>(backupRecordersCount + 1);
        for (int i = 0; i < backupRecordersCount + 1; ++i) {
            recordersList.add(new Recorders(new AtomicReferenceArray<Recorder>(MAX_NUM)));
        }

        curRecorders = recordersList.get(curIndex);
        return true;
    }

    private boolean initBackgroundTask() {
        try {
            backgroundExecutor = new ScheduledThreadPoolExecutor(1,
                    newThreadFactory("MyPerf4J-BackgroundExecutor_"),
                    new DiscardOldestPolicy());

            ExecutorManager.addExecutorService(backgroundExecutor);
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initBackgroundTask()", e);
        }
        return false;
    }

    public abstract boolean initOther();

    protected Recorder createRecorder(int methodTagId, ProfilingParams params) {
        return DefaultRecorder.getInstance(methodTagId, params.mostTimeThreshold(), params.outThresholdCount());
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

            final Recorders lastRecorders = turnAroundRecorders(lastTimeSliceStartTime, millTimeSlice);
            backgroundExecutor.schedule(new ExportMetricsTask(methodMetricsExporter, lastRecorders), 10, MILLISECONDS);
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.run(long, long) error", e);
        } finally {
            tryDiscardDelayTask();
        }
    }

    private Recorders turnAroundRecorders(long lastTimeSliceStartTime, long millTimeSlice) {
        final Recorders lastRecorders = this.curRecorders;
        lastRecorders.setStartTime(lastTimeSliceStartTime);
        lastRecorders.setStopTime(lastTimeSliceStartTime + millTimeSlice);
        lastRecorders.setWriting(false);
        this.curRecorders = nextRecorders(lastTimeSliceStartTime, millTimeSlice);
        return lastRecorders;
    }

    private Recorders nextRecorders(long lastTimeSliceStartTime, long millTimeSlice) {
        curIndex = getNextIdx(curIndex, recordersList.size());
        Logger.debug("RecorderMaintainer.nextRecorders(long, long) curIndex=" + curIndex);

        final Recorders nextRecorders = recordersList.get(curIndex);
        nextRecorders.setStartTime(lastTimeSliceStartTime + millTimeSlice);
        nextRecorders.setStopTime(lastTimeSliceStartTime + 2 * millTimeSlice);
        nextRecorders.setWriting(true);
        nextRecorders.resetRecorder();
        return nextRecorders;
    }

    private int getNextIdx(int idx, int maxIdx) {
        return (idx + 1) % maxIdx;
    }

    private void tryDiscardDelayTask() {
        final BlockingQueue<Runnable> queue = backgroundExecutor.getQueue();
        if (queue.size() >= MAX_DELAY_TASKS) {
            queue.poll();
            Logger.warn("RecorderMaintainer.tryDiscardDelayTask the backgroundExecutor delay task count reach " +
                    MAX_DELAY_TASKS + ", so discard oldest one!");
        }
    }

    @Override
    public String name() {
        return "RecorderMaintainer";
    }

    private static final class ExportMetricsTask implements Runnable {

        private final MethodTagMaintainer methodTagMaintainer;

        private final MethodMetricsExporter metricsExporter;

        private final Recorders lastRecorders;

        private ExportMetricsTask(MethodMetricsExporter metricsExporter, Recorders lastRecorders) {
            this.methodTagMaintainer = MethodTagMaintainer.getInstance();
            this.metricsExporter = metricsExporter;
            this.lastRecorders = lastRecorders;
        }

        @Override
        public void run() {
            if (lastRecorders.isWriting()) {
                Logger.warn("ExportMetricsTask.run() the lastRecorders is writing! " +
                        "Please increase `" + TIME_SLICE_METHOD + "` or `" + BACKUP_COUNT + "`!P1");
                return;
            }

            final long start = System.currentTimeMillis();
            final long epochStartMillis = lastRecorders.getStartTime();
            final long epochEndMillis = lastRecorders.getStopTime();
            try {
                metricsExporter.beforeProcess(epochStartMillis, epochStartMillis, epochEndMillis);
                final int actualSize = methodTagMaintainer.getMethodTagCount();
                for (int i = 0; i < actualSize; ++i) {
                    if (lastRecorders.isWriting()) {
                        Logger.warn("ExportMetricsTask.run() the lastRecorders is writing! " +
                                "Please increase `" + TIME_SLICE_METHOD + "` or `" + BACKUP_COUNT + "`!P2");
                        break;
                    }

                    final Recorder recorder = lastRecorders.getRecorder(i);
                    if (recorder == null) {
                        continue;
                    } else if (!recorder.hasRecord()) {
                        recordNoneMetrics(recorder.getMethodTagId());
                        continue;
                    }

                    final MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
                    final MethodMetrics metrics = calMetrics(recorder, methodTag, epochStartMillis, epochEndMillis);
                    MethodMetricsHistogram.recordMetrics(metrics);
                    metricsExporter.process(metrics, epochStartMillis, epochStartMillis, epochEndMillis);
                }
            } catch (Throwable t) {
                Logger.error("ExportMetricsTask.run() error", t);
            } finally {
                metricsExporter.afterProcess(epochStartMillis, epochStartMillis, epochEndMillis);
                final long cost = System.currentTimeMillis() - start;
                Logger.debug("ExportMetricsTask.run() finished!!! cost: " + cost + "ms");
            }
        }
    }
}
