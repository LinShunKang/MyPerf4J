package cn.myperf4j.core;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.processor.MethodMetricsProcessor;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.ThreadUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class AsyncMethodMetricsProcessor implements MethodMetricsProcessor {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(500), ThreadUtils.newThreadFactory("MyPerf4J-AsyncPerfStatsProcessor_"), new ThreadPoolExecutor.DiscardPolicy());

    private static AsyncMethodMetricsProcessor instance = null;

    private final MethodMetricsProcessor target;

    public AsyncMethodMetricsProcessor(MethodMetricsProcessor target) {
        this.target = target;
    }

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        target.beforeProcess(processId, startMillis, stopMillis);
    }

    @Override
    public void process(final MethodMetrics metrics, final long processId, final long startMillis, final long stopMillis) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (target != null) {
                        target.process(metrics, processId, startMillis, stopMillis);
                    }
                } catch (Exception e) {
                    Logger.error("AsyncMethodMetricsProcessor.start()", e);
                }
            }
        });
    }

    @Override
    public void afterProcess(final long processId, final long startMillis, final long stopMillis) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                target.afterProcess(processId, startMillis, stopMillis);
            }
        });
    }

    public static synchronized AsyncMethodMetricsProcessor initial(MethodMetricsProcessor target) {
        if (instance != null) {
            return instance;
        }
        return instance = new AsyncMethodMetricsProcessor(target);
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static AsyncMethodMetricsProcessor getInstance() {
        return instance;
    }
}
