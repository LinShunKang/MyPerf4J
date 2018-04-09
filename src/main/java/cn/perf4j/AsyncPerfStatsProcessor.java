package cn.perf4j;

import cn.perf4j.util.Logger;
import cn.perf4j.util.MyProperties;
import cn.perf4j.util.ThreadUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class AsyncPerfStatsProcessor implements PerfStatsProcessor {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(500), ThreadUtils.newThreadFactory("MyPerf4J-AsyncPerfStatsProcessor_"), new ThreadPoolExecutor.DiscardPolicy());

    private static final AsyncPerfStatsProcessor instance = new AsyncPerfStatsProcessor();

    private static PerfStatsProcessor target = null;

    static {
        try {
            String className = MyProperties.getStr(PropConstants.PERF_STATS_PROCESSOR);
            if (className == null || className.isEmpty()) {
                Logger.error("MyPerf4J.PSP NOT FOUND, MyPerf4J not running!!!");
                MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_NO);
            } else {
                Class<?> clazz = AsyncPerfStatsProcessor.class.getClassLoader().loadClass(className);
                Object obj = clazz.newInstance();
                if (obj instanceof PerfStatsProcessor) {
                    target = (PerfStatsProcessor) obj;
                    MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_YES);
                    ShutdownHook.init();
                } else {
                    Logger.error("AsyncPerfStatsProcessor className is not correct!!! MyPerf4J not running!!!");
                    MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_NO);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Logger.error("AsyncPerfStatsProcessor static block error!!! MyPerf4J will not running!!!", e);
        }
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

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static AsyncPerfStatsProcessor getInstance() {
        return instance;
    }
}
