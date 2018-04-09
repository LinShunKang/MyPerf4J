package cn.perf4j;

import cn.perf4j.util.IOUtils;
import cn.perf4j.util.Logger;
import cn.perf4j.util.ThreadUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
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
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = AsyncPerfStatsProcessor.class.getClassLoader().getResourceAsStream("config/myPerf4J.properties");
            properties.load(in);
        } catch (IOException e) {
            Logger.error("AsyncPerfStatsProcessor load config/myPerf4J.properties", e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        String pspStr = properties.getProperty("MyPerf4J.PSP");
        if (pspStr == null || pspStr.isEmpty()) {
            Logger.error("MyPerf4J.PSP NOT FOUND!!!");
            throw new IllegalArgumentException("MyPerf4J.PSP NOT FOUND!!!");
        }

        try {
            Class<?> clazz = AsyncPerfStatsProcessor.class.getClassLoader().loadClass(pspStr);
            Object obj = clazz.newInstance();
            if (obj instanceof PerfStatsProcessor) {
                target = (PerfStatsProcessor) obj;
            } else {
                Logger.error("AsyncPerfStatsProcessor pspName is not correct!!!");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Logger.error("AsyncPerfStatsProcessor", e);
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
            e.printStackTrace();
        }
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public static AsyncPerfStatsProcessor getInstance() {
        return instance;
    }
}
