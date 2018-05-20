package cn.myperf4j.core;

import cn.myperf4j.base.PerfStats;
import cn.myperf4j.base.PerfStatsProcessor;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.ThreadUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class AsyncPerfStatsProcessor implements PerfStatsProcessor {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(500), ThreadUtils.newThreadFactory("MyPerf4J-AsyncPerfStatsProcessor_"), new ThreadPoolExecutor.DiscardPolicy());

    private static AsyncPerfStatsProcessor instance = null;

    private final PerfStatsProcessor target;

    public AsyncPerfStatsProcessor(PerfStatsProcessor target) {
        this.target = target;
    }

    @Override
    public void process(final List<PerfStats> perfStatsList, final long startMillis, final long stopMillis) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (target != null) {
                        target.process(perfStatsList, startMillis, stopMillis);
                    }
                }
            });
        } catch (Exception e) {
            Logger.error("AsyncPerfStatsProcessor.process(" + perfStatsList + ", " + startMillis + ", " + startMillis + "", e);
        }
    }

    public static synchronized AsyncPerfStatsProcessor initial(PerfStatsProcessor target) {
        if (instance != null) {
            return instance;
        }
        return instance = new AsyncPerfStatsProcessor(target);
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static AsyncPerfStatsProcessor getInstance() {
        return instance;
    }
}
