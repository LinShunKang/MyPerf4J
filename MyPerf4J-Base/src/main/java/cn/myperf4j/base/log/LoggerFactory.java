package cn.myperf4j.base.log;

public class LoggerFactory {

    public static ILogger getLogger(String logFile) {
        return new DailyRollingLogger(logFile);
    }

}
