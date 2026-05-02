package cn.myperf4j.base.util;

import static cn.myperf4j.base.util.text.DateFormatUtils.formatToMillis;
import static java.lang.System.currentTimeMillis;

/**
 * Created by LinShunkang on 2018/3/20
 */
public final class Logger {

    private static boolean debugEnable;

    private static final String PREFIX = " [MyPerf4J] ";

    private static final String INFO_LEVEL = "INFO ";

    private static final String DEBUG_LEVEL = "DEBUG ";

    private static final String WARN_LEVEL = "WARN ";

    private static final String ERROR_LEVEL = "ERROR ";

    private Logger() {
        //empty
    }

    public static void setDebugEnable(boolean debugEnable) {
        Logger.debugEnable = debugEnable;
    }

    public static boolean isDebugEnable() {
        return debugEnable;
    }

    public static void info(String msg) {
        System.out.println(getPrefix(INFO_LEVEL) + msg);
    }

    private static String getPrefix(String logLevel) {
        return formatToMillis(currentTimeMillis()) + PREFIX + logLevel + "[" + Thread.currentThread().getName() + "] ";
    }

    public static void debug(String msg) {
        if (debugEnable) {
            System.out.println(getPrefix(DEBUG_LEVEL) + msg);
        }
    }

    public static void warn(String msg) {
        System.out.println(getPrefix(WARN_LEVEL) + msg);
    }

    public static void warn(String msg, Throwable throwable) {
        synchronized (System.out) {
            System.out.println(getPrefix(WARN_LEVEL) + msg + " " + throwable.getMessage());
            throwable.printStackTrace(System.out);
        }
    }

    public static void error(String msg) {
        System.err.println(getPrefix(ERROR_LEVEL) + msg);
    }

    public static void error(String msg, Throwable throwable) {
        synchronized (System.err) {
            System.err.println(getPrefix(ERROR_LEVEL) + msg + " " + throwable.getMessage());
            throwable.printStackTrace(System.err);
        }
    }
}
