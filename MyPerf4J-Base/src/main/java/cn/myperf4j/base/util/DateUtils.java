package cn.myperf4j.base.util;

import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public final class DateUtils {

    private DateUtils() {
        //empty
    }

    public static boolean isSameDay(Date date1, Date date2) {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(YEAR) == cal2.get(YEAR)
                && cal1.get(MONTH) == cal2.get(MONTH)
                && cal1.get(DAY_OF_MONTH) == cal2.get(DAY_OF_MONTH);
    }

    public static boolean isSameHour(Date date1, Date date2) {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(YEAR) == cal2.get(YEAR)
                && cal1.get(MONTH) == cal2.get(MONTH)
                && cal1.get(DAY_OF_MONTH) == cal2.get(DAY_OF_MONTH)
                && cal1.get(HOUR_OF_DAY) == cal2.get(HOUR_OF_DAY);
    }

    public static boolean isSameMinute(Date date1, Date date2) {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(YEAR) == cal2.get(YEAR)
                && cal1.get(MONTH) == cal2.get(MONTH)
                && cal1.get(DAY_OF_MONTH) == cal2.get(DAY_OF_MONTH)
                && cal1.get(HOUR_OF_DAY) == cal2.get(HOUR_OF_DAY)
                && cal1.get(MINUTE) == cal2.get(MINUTE);
    }
}
