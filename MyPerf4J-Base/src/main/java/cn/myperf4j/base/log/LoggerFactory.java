package cn.myperf4j.base.log;

import cn.myperf4j.base.constant.PropertyValues;

import java.util.HashMap;
import java.util.Map;

public class LoggerFactory {

    private static final Map<String, ILogger> LOGGER_MAP = new HashMap<>();

    public static synchronized ILogger getLogger(String logFile) {
        logFile = logFile.trim();

        if (logFile.equalsIgnoreCase(PropertyValues.NULL_FILE)) {
            return new NullLogger();
        }

        ILogger logger = LOGGER_MAP.get(logFile);
        if (logger != null) {
            return logger;
        }

        logger = new DailyRollingLogger(logFile);
        LOGGER_MAP.put(logFile, logger);
        return logger;
    }

}
