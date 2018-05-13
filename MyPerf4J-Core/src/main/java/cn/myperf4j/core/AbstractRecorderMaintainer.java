package cn.myperf4j.core;

import cn.myperf4j.core.annotation.Profiler;
import cn.myperf4j.core.config.ProfilerParams;
import cn.myperf4j.core.constant.PropertyValues;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.PerfStatsCalculator;
import cn.myperf4j.core.util.ThreadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/4/25
 */
public abstract class AbstractRecorderMaintainer {

    protected final Object locker = new Object();

    private List<AbstractRecorder> tempRecorderList = new ArrayList<>(1024);//内存复用，避免每一次轮转都生成一个大对象

    protected volatile Map<String, AbstractRecorder> recorderMap;

    protected volatile Map<String, AbstractRecorder> backupRecorderMap;

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

    public abstract void addRecorder(String tag, ProfilerParams params);

    public AbstractRecorder getRecorder(String api) {
        return recorderMap.get(api);
    }

    public Map<String, AbstractRecorder> getRecorderMap() {
        return Collections.unmodifiableMap(recorderMap);
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
                    for (Map.Entry<String, AbstractRecorder> entry : recorderMap.entrySet()) {
                        AbstractRecorder curRecorder = entry.getValue();
                        if (curRecorder.getStartTime() <= 0L || curRecorder.getStopTime() <= 0L) {
                            curRecorder.setStartTime(currentMills - millTimeSlice);
                            curRecorder.setStopTime(currentMills);
                        }
                    }

                    for (Map.Entry<String, AbstractRecorder> entry : backupRecorderMap.entrySet()) {
                        AbstractRecorder backupRecorder = entry.getValue();
                        backupRecorder.resetRecord();
                        backupRecorder.setStartTime(currentMills);
                        backupRecorder.setStopTime(currentMills + millTimeSlice);
                    }

                    Map<String, AbstractRecorder> tmpMap = recorderMap;
                    recorderMap = backupRecorderMap;
                    backupRecorderMap = tmpMap;
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
                    tempRecorderList.addAll(backupRecorderMap.values());
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
