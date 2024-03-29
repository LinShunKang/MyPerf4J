package cn.myperf4j.base.file;

import cn.myperf4j.base.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/9/7
 */
public class DailyRollingFileWriter extends AutoRollingFileWriter {

    private static final ThreadLocal<SimpleDateFormat> FILE_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("'.'yyyy-MM-dd");
        }
    };

    public DailyRollingFileWriter(String fileName, int reserveFileCount) {
        super(fileName, reserveFileCount);
    }

    @Override
    Calendar computeEpochCal(Date now, int epochOffset) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, epochOffset);
        return cal;
    }

    @Override
    String formatDateFileName(String fileName, Date date) {
        return fileName + FILE_DATE_FORMAT.get().format(date);
    }

    @Override
    boolean isSameEpoch(Date date1, Date date2) {
        return DateUtils.isSameDay(date1, date2);
    }
}
