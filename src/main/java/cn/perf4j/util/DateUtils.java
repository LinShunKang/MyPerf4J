package cn.perf4j.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/3/18
 */
public final class DateUtils {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final  DateFormat DEFALUT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_FORMAT);

    public static String getDateStr(long millis) {
        return getDateStr(new Date(millis));
    }

    public static String getDateStr(Date date) {
        return DEFALUT_DATE_FORMAT.format(date);
    }

    public static String getDateStr(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

}
