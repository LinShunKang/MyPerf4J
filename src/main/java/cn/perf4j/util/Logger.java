package cn.perf4j.util;

import java.util.Date;

/**
 * Created by LinShunkang on 2018/3/20
 */
public final class Logger {

    public static void info(String msg) {
        System.out.println(getPrefix() + msg);
    }

    private static String getPrefix() {
        return DateUtils.getToMillisStr(new Date()) + " [MyPerf4J] ";
    }

    public static void error(String msg) {
        System.err.println(getPrefix() + msg);
    }

}
