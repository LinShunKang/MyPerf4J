package cn.myperf4j.base.log;

import cn.myperf4j.base.util.DailyRollingFileWriter;
import cn.myperf4j.base.util.ExecutorManager;
import cn.myperf4j.base.util.ThreadUtils;

import java.util.*;
import java.util.concurrent.*;

public class DailyRollingLogger implements ILogger {

    private static final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1,
            ThreadUtils.newThreadFactory("MyPerf4J-DailyRollingLogger-"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private static final Set<DailyRollingFileWriter> WRITER_LIST = new CopyOnWriteArraySet<>();

    static {
        ExecutorManager.addExecutorService(scheduledExecutor);

        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (DailyRollingFileWriter writer : WRITER_LIST) {
                    writer.flush();
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                for (DailyRollingFileWriter writer : WRITER_LIST) {
                    writer.preCloseFile();
                }

                for (DailyRollingFileWriter writer : WRITER_LIST) {
                    writer.closeFile();
                }
            }
        }));
    }

    private final DailyRollingFileWriter writer;

    DailyRollingLogger(String logFile) {
        this.writer = new DailyRollingFileWriter(logFile);
        WRITER_LIST.add(writer);
    }

    @Override
    public void log(String msg) {
        writer.write(msg + '\n');
    }

    @Override
    protected void finalize() {
        WRITER_LIST.remove(writer);
        writer.closeFile();
    }
}
