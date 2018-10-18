package cn.myperf4j.base.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/8/22
 */
public class DateFormatUtils {

    private static final ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };


    public static String format(long millis) {
        return DEFAULT_DATE_FORMAT.get().format((new Date(millis)));
    }
}
