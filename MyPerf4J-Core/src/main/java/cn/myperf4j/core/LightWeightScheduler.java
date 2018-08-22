package cn.myperf4j.core;

import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.ThreadUtils;

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

    public static void initScheduleTask(final List<Scheduler> schedulerList,
                                        long initialDelay,
                                        long period,
                                        TimeUnit unit) {
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < schedulerList.size(); ++i) {
                    try {
                        schedulerList.get(i).run();
                    } catch (Exception e) {
                        Logger.error("LightWeightScheduler.run()", e);
                    }
                }
            }
        }, initialDelay, period, unit);
    }

}
