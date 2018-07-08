package cn.myperf4j.base.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/3/18
 */
public final class DateUtils {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DEFAULT_FORMAT);
        }
    };

    private static final String TO_MILLS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final ThreadLocal<DateFormat> TO_MILLS_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(TO_MILLS_FORMAT);
        }
    };

    public static String getStr(long millis) {
        return getStr(new Date(millis));
    }

    public static String getStr(Date date) {
        return DEFAULT_DATE_FORMAT.get().format(date);
    }

    public static String getToMillisStr(Date date) {
        return TO_MILLS_DATE_FORMAT.get().format(date);
    }

    public static String getStr(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

}
