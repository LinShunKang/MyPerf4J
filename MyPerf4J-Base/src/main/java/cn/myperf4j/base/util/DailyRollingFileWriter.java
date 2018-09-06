package cn.myperf4j.base.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 该类参考自TProfiler: https://github.com/alibaba/TProfiler/blob/master/src/main/java/com/taobao/profile/utils/DailyRollingFileWriter.java
 */
public class DailyRollingFileWriter {

    private static final ThreadLocal<SimpleDateFormat> FILE_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("'.'yyyy-MM-dd");
        }
    };

    private final String fileName;

    private volatile String rollingFileName;

    private volatile BufferedWriter bufferedWriter;

    private volatile boolean closed;

    private volatile long nextRollingTime;


    public DailyRollingFileWriter(String fileName) {
        Date now = new Date();

        this.fileName = fileName;
        this.closed = false;
        this.nextRollingTime = getNextRollingTime(now);
        this.rollingFileName = fileName + FILE_DATE_FORMAT.get().format(now);

        File targetFile = new File(fileName);
        if (!targetFile.exists()) {
            createWriter(targetFile, false);
            return;
        }

        Date lastModifiedDate = new Date(targetFile.lastModified());
        if (DateUtils.isSameDate(now, lastModifiedDate)) {
            createWriter(targetFile, true);
            return;
        }

        this.rollingFileName = fileName + FILE_DATE_FORMAT.get().format(lastModifiedDate);
        rollingFile(now);
    }

    private long getNextRollingTime(Date now) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, 1);
        return cal.getTime().getTime();
    }

    private void createWriter(File file, boolean append) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                Logger.debug("DailyRollingFileWriter.createWriter(" + file.getName() + ", " + append + "): mkdirs=" + parent.mkdirs());
            }

            bufferedWriter = new BufferedWriter(new FileWriter(file, append), 64 * 1024);
        } catch (IOException e) {
            Logger.error("DailyRollingFileWriter.createWriter(" + file.getName() + ", " + append + ")", e);
        }
    }

    private void rollingFile(Date now) {
        String datedFilename = fileName + FILE_DATE_FORMAT.get().format(now);
        if (rollingFileName.equals(datedFilename)) {
            return;
        }

        closeFile();

        File targetFile = new File(rollingFileName);
        if (targetFile.exists()) {
            Logger.debug("DailyRollingFileWriter.rollingFile(" + now + "): delete rollingFile=" + targetFile.delete());
        }

        File file = new File(fileName);
        Logger.debug("DailyRollingFileWriter.rollingFile(" + now + "): rename " + fileName + " to " + targetFile.getName() + " " + file.renameTo(targetFile));

        createWriter(new File(fileName), false);
        rollingFileName = datedFilename;
    }

    public void write(String msg) {
        long time = System.currentTimeMillis();
        if (time > nextRollingTime) {
            Date now = new Date();
            nextRollingTime = getNextRollingTime(now);
            rollingFile(now);
        }
        write0(msg);
    }

    private void write0(String msg) {
        try {
            if (bufferedWriter != null && !closed) {
                bufferedWriter.write(msg);
            }
        } catch (Exception e) {
            Logger.error("DailyRollingFileWriter.write0(msg)", e);
        }
    }

    public void writeAndFlush(String msg) {
        write0(msg);
        flush();
    }

    public void flush() {
        try {
            if (bufferedWriter != null && !closed) {
                bufferedWriter.flush();
            }
        } catch (Exception e) {
            Logger.error("DailyRollingFileWriter.flush()", e);
        }
    }

    public void closeFile() {
        try {
            closed = true;
            if (bufferedWriter != null) {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            Logger.error("DailyRollingFileWriter.closeFile()", e);
        }
    }

    public void preCloseFile() {
        closed = true;
    }
}
