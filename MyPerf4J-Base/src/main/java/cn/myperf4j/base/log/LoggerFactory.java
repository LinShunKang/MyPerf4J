package cn.myperf4j.base.log;

import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.constant.PropertyValues.Metrics;

import java.util.HashMap;
import java.util.Map;

public final class LoggerFactory {

    private static final MetricsConfig METRICS_CONFIG = ProfilingConfig.metricsConfig();

    private static final Map<String, ILogger> LOGGER_MAP = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                for (ILogger writer : LOGGER_MAP.values()) {
                    writer.preCloseLog();
                }

                for (ILogger writer : LOGGER_MAP.values()) {
                    writer.closeLog();
                }
            }
        }));
    }

    public static synchronized ILogger getLogger(String logFile) {
        logFile = logFile.trim();

        if (logFile.equalsIgnoreCase(Metrics.NULL_FILE)) {
            return new NullLogger();
        } else if (logFile.equalsIgnoreCase(Metrics.STDOUT_FILE)) {
            return new StdoutLogger();
        }

        ILogger logger = LOGGER_MAP.get(logFile);
        if (logger != null) {
            return logger;
        }

        logger = new AutoRollingLogger(logFile, METRICS_CONFIG.logRollingTimeUnit(), METRICS_CONFIG.logReserveCount());
        LOGGER_MAP.put(logFile, logger);
        return logger;
    }
}
