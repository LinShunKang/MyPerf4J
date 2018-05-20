package cn.myperf4j.core.util;

import java.util.Date;

/**
 * Created by LinShunkang on 2018/3/20
 */
public final class Logger {

    private static boolean debugEnable = false;

    private static final String PREFIX = " [MyPerf4J] ";

    private static final String INFO_LEVEL = "INFO ";

    private static final String DEBUG_LEVEL = "DEBUG ";

    private static final String WARN_LEVEL = "WARN ";

    private static final String ERROR_LEVEL = "ERROR ";

    public static void setDebugEnable(boolean debugEnable) {
        Logger.debugEnable = debugEnable;
    }

    public static void info(String msg) {
        System.out.println(getPrefix(INFO_LEVEL) + msg);
    }

    private static String getPrefix(String logLevel) {
        return DateUtils.getToMillisStr(new Date()) + PREFIX + logLevel;
    }

    public static void debug(String msg) {
        if (debugEnable) {
            System.out.println(getPrefix(DEBUG_LEVEL) + msg);
        }
    }

    public static void warn(String msg) {
        System.out.println(getPrefix(WARN_LEVEL) + msg);
    }

    public static void error(String msg) {
        System.err.println(getPrefix(ERROR_LEVEL) + msg);
    }

    public static void error(String msg, Throwable throwable) {
        synchronized (System.err) {
            System.err.println(getPrefix(ERROR_LEVEL) + msg);
            throwable.printStackTrace();
        }
    }
}
