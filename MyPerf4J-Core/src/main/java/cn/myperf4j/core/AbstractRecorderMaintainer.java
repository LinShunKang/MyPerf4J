package cn.myperf4j.core;

import cn.myperf4j.base.PerfStats;
import cn.myperf4j.base.PerfStatsProcessor;
import cn.myperf4j.base.annotation.Profiler;
import cn.myperf4j.core.config.ProfilerParams;
import cn.myperf4j.core.constant.PropertyValues;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.PerfStatsCalculator;
import cn.myperf4j.core.util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/4/25
 */
public abstract class AbstractRecorderMaintainer {

    protected final Object locker = new Object();

    private List<AbstractRecorder> tempRecorderList = new ArrayList<>(1024);//内存复用，避免每一次轮转都生成一个大对象

    protected volatile AtomicReferenceArray<AbstractRecorder> recorders;

    protected volatile AtomicReferenceArray<AbstractRecorder> backupRecorders;

    private final ScheduledThreadPoolExecutor roundRobinExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtils.newThreadFactory("MyPerf4J-RoundRobinExecutor_"), new ThreadPoolExecutor.DiscardPolicy());

    private final ScheduledThreadPoolExecutor backgroundExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtils.newThreadFactory("MyPerf4J-BackgroundExecutor_"), new ThreadPoolExecutor.DiscardPolicy());

    private PerfStatsProcessor perfStatsProcessor;

    protected boolean accurateMode;

    private long millTimeSlice;

    private volatile long nextTimeSliceEndTime = 0L;

    private volatile boolean backupRecorderReady = false;

    public boolean initial(PerfStatsProcessor processor, boolean accurateMode, long millTimeSlice) {
        this.perfStatsProcessor = processor;
        this.accurateMode = accurateMode;
        this.millTimeSlice = getFitMillTimeSlice(millTimeSlice);

        if (!initRecorderMap()) {
            return false;
        }

        if (!initRoundRobinTask()) {
            return false;
        }

        if (!initBackgroundTask()) {
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

    public abstract boolean initRecorderMap();

    private boolean initRoundRobinTask() {
        try {
            roundRobinExecutor.scheduleAtFixedRate(new RoundRobinProcessor(), 0, 500, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initRoundRobinTask()", e);
        }
        return false;
    }

    private boolean initBackgroundTask() {
        try {
            backgroundExecutor.scheduleAtFixedRate(new BackgroundProcessor(), 0, 1, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initBackgroundTask()", e);
        }
        return false;
    }

    public abstract boolean initOther();

    protected AbstractRecorder createRecorder(String api, Profiler profiler) {
        return createRecorder(api, profiler.mostTimeThreshold(), profiler.outThresholdCount());
    }

    protected AbstractRecorder createRecorder(String api, int mostTimeThreshold, int outThresholdCount) {
        if (accurateMode) {
            return AccurateRecorder.getInstance(api, mostTimeThreshold, outThresholdCount);
        }
        return RoughRecorder.getInstance(api, mostTimeThreshold);
    }

    public List<AbstractRecorder> getRecorders() {
        List<AbstractRecorder> tempRecorderList = new ArrayList<>(256);
        for (int i = 0; i < recorders.length(); ++i) {
            AbstractRecorder recorder = recorders.get(i);
            if (recorder == null) {
                break;
            }
            tempRecorderList.add(recorder);
        }
        return tempRecorderList;
    }

    public abstract void addRecorder(int tagId, String tag, ProfilerParams params);

    public AbstractRecorder getRecorder(int tagId) {
        return recorders.get(tagId);
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

            backupRecorderReady = false;
            try {
                synchronized (locker) {
                    for (int i = 0; i < recorders.length(); ++i) {
                        AbstractRecorder curRecorder = recorders.get(i);
                        if (curRecorder == null) {
                            break;
                        }

                        if (curRecorder.getStartTime() <= 0L || curRecorder.getStopTime() <= 0L) {
                            curRecorder.setStartTime(currentMills - millTimeSlice);
                            curRecorder.setStopTime(currentMills);
                        }
                    }

                    for (int i = 0; i < backupRecorders.length(); ++i) {
                        AbstractRecorder backupRecorder = backupRecorders.get(i);
                        if (backupRecorder == null) {
                            break;
                        }

                        backupRecorder.resetRecord();
                        backupRecorder.setStartTime(currentMills);
                        backupRecorder.setStopTime(currentMills + millTimeSlice);
                    }

                    AtomicReferenceArray<AbstractRecorder>  tmpMap = recorders;
                    recorders = backupRecorders;
                    backupRecorders = tmpMap;
                }
                Logger.info("RoundRobinProcessor finished!!!!");
            } catch (Exception e) {
                Logger.error("RecorderMaintainer.roundRobinExecutor error", e);
            } finally {
                backupRecorderReady = true;
            }
        }
    }

    private class BackgroundProcessor implements Runnable {

        @Override
        public void run() {
            if (!backupRecorderReady) {
                return;
            }

            try {
                synchronized (locker) {
                    for (int i = 0; i < backupRecorders.length(); ++i) {
                        AbstractRecorder recorder = backupRecorders.get(i);
                        if (recorder == null) {
                            break;
                        }
                        tempRecorderList.add(recorder);
                    }
                }

                AbstractRecorder recorder = null;
                List<PerfStats> perfStatsList = new ArrayList<>(tempRecorderList.size());
                for (int i = 0; i < tempRecorderList.size(); ++i) {
                    recorder = tempRecorderList.get(i);
                    perfStatsList.add(PerfStatsCalculator.calPerfStats(recorder));
                }

                if (recorder != null) {
                    perfStatsProcessor.process(perfStatsList, recorder.getStartTime(), recorder.getStopTime());
                }

                Logger.info("BackgroundProcessor finished!!!!");
            } catch (Exception e) {
                Logger.error("RecorderMaintainer.backgroundExecutor error", e);
            } finally {
                backupRecorderReady = false;
                tempRecorderList.clear();
            }
        }
    }
}
