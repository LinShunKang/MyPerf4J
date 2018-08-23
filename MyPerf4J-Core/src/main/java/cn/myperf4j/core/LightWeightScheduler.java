package cn.myperf4j.core;

import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.ThreadUtils;
import cn.myperf4j.core.scheduler.Scheduler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/8/22
 */
public class LightWeightScheduler {

    private static final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(2,
            ThreadUtils.newThreadFactory("MyPerf4J-LightWeightScheduler-"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private final List<Scheduler> schedulerList;

    private final long initialDelay;

    private final long period;

    private final TimeUnit unit;

    private final long millTimeSlice;

    private volatile long nextTimeSliceEndTime = 0L;

    public LightWeightScheduler(List<Scheduler> schedulerList,
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

    public static void initScheduleTask(List<Scheduler> schedulerList, long millTimeSlice) {
        ExecutorServiceManager.addExecutorService(scheduledExecutor);

        millTimeSlice = getFitMillTimeSlice(millTimeSlice);
        new LightWeightScheduler(schedulerList, 0, 10, TimeUnit.MILLISECONDS, millTimeSlice).start();
    }

    private static long getFitMillTimeSlice(long millTimeSlice) {
        if (millTimeSlice < PropertyValues.MIN_TIME_SLICE) {
            return PropertyValues.MIN_TIME_SLICE;
        } else if (millTimeSlice > PropertyValues.MAX_TIME_SLICE) {
            return PropertyValues.MAX_TIME_SLICE;
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

                runTasks(currentMills);
            }
        }, initialDelay, period, unit);
    }

    private void runTasks(long currentMills) {
        try {
            for (int i = 0; i < schedulerList.size(); ++i) {
                runTask(schedulerList.get(i), currentMills);
            }
        } finally {
            Logger.debug("LightWeightScheduler.runTasks() cost: " + (System.currentTimeMillis() - currentMills) + "ms");
        }
    }

    private void runTask(Scheduler scheduler, long currentMills) {
        try {
            scheduler.run(currentMills, millTimeSlice, nextTimeSliceEndTime);
        } catch (Exception e) {
            Logger.error("LightWeightScheduler.runTask(" + scheduler + ")", e);
        }
    }

}
