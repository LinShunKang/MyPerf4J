package cn.myperf4j.base.util.text;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by LinShunkang on 2018/8/22
 */
public final class DateFormatUtils {

    private static final DateTimeFormatter STANDARD_SECONDS_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private static final DateTimeFormatter STANDARD_MILLIS_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    public static String formatToSeconds(long millis) {
        return STANDARD_SECONDS_FORMATTER.format(Instant.ofEpochMilli(millis));
    }

    public static String formatToMillis(long millis) {
        return STANDARD_MILLIS_FORMATTER.format(Instant.ofEpochMilli(millis));
    }

    private DateFormatUtils() {
        //empty
    }
}
