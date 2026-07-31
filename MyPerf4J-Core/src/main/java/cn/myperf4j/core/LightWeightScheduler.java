package cn.myperf4j.core;

import cn.myperf4j.base.Scheduler;
import cn.myperf4j.base.constant.PropertyValues.Metrics;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.concurrent.ExecutorManager;
import cn.myperf4j.base.util.concurrent.ThreadUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/8/22
 */
public final class LightWeightScheduler {

    private static final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(2,
            ThreadUtils.newThreadFactory("MyPerf4J-LightWeightScheduler-"),
            new DiscardOldestPolicy());

    static {
        ExecutorManager.addExecutorService(scheduledExecutor);
    }

    private final List<Scheduler> schedulerList;

    private final long initialDelay;

    private final long period;

    private final TimeUnit unit;

    private final long millTimeSlice;

    private volatile long nextTimeSliceEndTime;

    private LightWeightScheduler(List<Scheduler> schedulerList,
                                 long initialDelay,
                                 long period,
                                 TimeUnit unit,
                                 long millTimeSlice) {
        this.schedulerList = Collections.unmodifiableList(schedulerList);
        this.millTimeSlice = millTimeSlice;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }

    public static void dispatchScheduleTask(Scheduler scheduler, long millTimeSlice) {
        dispatchScheduleTask(Collections.singletonList(scheduler), millTimeSlice);
    }

    public static void dispatchScheduleTask(List<Scheduler> schedulerList, long millTimeSlice) {
        millTimeSlice = getFitMillTimeSlice(millTimeSlice);
        new LightWeightScheduler(schedulerList, 0, 10, TimeUnit.MILLISECONDS, millTimeSlice).start();
    }

    private static long getFitMillTimeSlice(long millTimeSlice) {
        if (millTimeSlice < Metrics.MIN_TIME_SLICE) {
            return Metrics.MIN_TIME_SLICE;
        } else if (millTimeSlice > Metrics.MAX_TIME_SLICE) {
            return Metrics.MAX_TIME_SLICE;
        }
        return millTimeSlice;
    }

    private void start() {
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
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

                runAllTasks(currentMills);
            }
        }, initialDelay, period, unit);
    }

    private void runAllTasks(long currentMills) {
        final long lastTimeSliceStartTime = currentMills - millTimeSlice;
        for (int i = 0; i < schedulerList.size(); ++i) {
            runTask(schedulerList.get(i), lastTimeSliceStartTime);
        }
    }

    private void runTask(Scheduler scheduler, long lastTimeSliceStartTime) {
        final long startMills = System.currentTimeMillis();
        try {
            scheduler.run(lastTimeSliceStartTime, millTimeSlice);
        } catch (Exception e) {
            Logger.error("LightWeightScheduler.runTask(" + scheduler + ", " + lastTimeSliceStartTime + ")", e);
        } finally {
            Logger.debug("LightWeightScheduler.runTask(" + scheduler.name() + ", " + lastTimeSliceStartTime
                    + ") cost: " + (System.currentTimeMillis() - startMills) + "ms");
        }
    }
}
