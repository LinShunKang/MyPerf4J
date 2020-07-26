package cn.myperf4j.base.file;

import cn.myperf4j.base.util.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/9/7
 * 该类参考自TProfiler:
 * https://github.com/alibaba/TProfiler/blob/master/src/main/java/com/taobao/profile/utils/DailyRollingFileWriter.java
 */
public abstract class AutoRollingFileWriter {

    private static final int MAX_INACTIVITY_EPOCHS = 30;

    private final String fileName;

    private final int reserveFileCount;

    private volatile String rollingFileName;

    private volatile BufferedWriter bufferedWriter;

    private volatile boolean closed;

    private volatile long nextRollingTime;

    public AutoRollingFileWriter(String fileName, int reserveFileCount) {
        Date now = new Date();

        this.fileName = fileName;
        this.reserveFileCount = Math.max(reserveFileCount, 0);
        this.closed = false;
        this.nextRollingTime = getNextRollingTime(now);
        this.rollingFileName = formatDateFileName(fileName, now);

        try {
            File targetFile = new File(fileName);
            if (!targetFile.exists()) {
                createWriter(targetFile, false);
                return;
            }

            Date lastModifiedDate = new Date(targetFile.lastModified());
            if (isSameEpoch(now, lastModifiedDate)) {
                createWriter(targetFile, true);
                return;
            }

            this.rollingFileName = formatDateFileName(fileName, lastModifiedDate);
            rollingFile(now);
        } finally {
            clean(now, MAX_INACTIVITY_EPOCHS); //尽可能的删除过期的日志文件
        }
    }

    private void clean(Date now, int epochs) {
        for (int i = 0; i < epochs; ++i) {
            int epochOffset = (-reserveFileCount - 1) - i;
            Date date = computeEpochCal(now, epochOffset).getTime();
            File file2Del = new File(formatDateFileName(fileName, date));
            if (file2Del.exists() && file2Del.isFile()) {
                boolean delete = file2Del.delete();
                Logger.info("AutoRollingFileWriter.clean(" + now + ", " + epochs + "): delete "
                        + file2Del.getName() + " " + (delete ? "success" : "fail"));
            }
        }
    }

    private long getNextRollingTime(Date now) {
        Calendar calendar = computeEpochCal(now, 1);
        return calendar.getTime().getTime();
    }

    abstract Calendar computeEpochCal(Date now, int epochOffset);

    abstract String formatDateFileName(String fileName, Date date);

    abstract boolean isSameEpoch(Date date1, Date date2);

    private void createWriter(File file, boolean append) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean mkdirs = parent.mkdirs();
                Logger.info("AutoRollingFileWriter.createWriter(" + file.getName() + ", "
                        + append + "): mkdirs=" + mkdirs);
            }

            bufferedWriter = new BufferedWriter(new FileWriter(file, append), 64 * 1024);
        } catch (IOException e) {
            Logger.error("AutoRollingFileWriter.createWriter(" + file.getName() + ", " + append + ")", e);
        }
    }

    private void rollingFile(Date now) {
        try {
            String datedFilename = formatDateFileName(fileName, now);
            if (rollingFileName.equals(datedFilename)) {
                Logger.info("AutoRollingFileWriter.rollingFile(" + now + "): rollingFile=" + rollingFileName
                        + ", datedFilename=" + datedFilename + " return!!!");
                return;
            }

            closeFile(false);

            File targetFile = new File(rollingFileName);
            if (targetFile.exists()) {
                boolean delete = targetFile.delete();
                Logger.info("AutoRollingFileWriter.rollingFile(" + now + "): rollingFile=" + rollingFileName
                        + ", delete=" + delete);
            }

            File file = new File(fileName);
            boolean rename = file.renameTo(targetFile);
            Logger.info("AutoRollingFileWriter.rollingFile(" + now + "): rename " + fileName + " to "
                    + targetFile.getName() + " " + (rename ? "success" : "fail"));

            createWriter(new File(fileName), false);
            rollingFileName = datedFilename;

            clean(now, 1); //删除最近一个过期的日志文件
        } catch (Exception e) {
            Logger.error("AutoRollingFileWriter.rollingFile(" + now + "): rollingFile=" + rollingFileName, e);
        }
    }

    public void write(String msg) {
        write0(msg);
    }

    private void write0(String msg) {
        long time = System.currentTimeMillis();
        if (time < nextRollingTime) {
            doWrite(msg);
            return;
        }

        synchronized (this) {
            if (time > nextRollingTime) {
                Date now = new Date();
                nextRollingTime = getNextRollingTime(now);
                rollingFile(now);
            }
        }

        doWrite(msg);
    }

    private void doWrite(String msg) {
        try {
            if (bufferedWriter != null && !closed) {
                bufferedWriter.write(msg);
            }
        } catch (Exception e) {
            Logger.error("AutoRollingFileWriter.doWrite(msg)", e);
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
            Logger.error("AutoRollingFileWriter.flush()", e);
        }
    }

    public void closeFile(boolean setCloseFlag) {
        try {
            closed = setCloseFlag;
            if (bufferedWriter != null) {
//                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            Logger.error("AutoRollingFileWriter.closeFile()", e);
        }
    }

    public void preCloseFile() {
        closed = true;
    }

    @Override
    public String toString() {
        return "AutoRollingFileWriter{" +
                "fileName='" + fileName + '\'' +
                ", rollingFileName='" + rollingFileName + '\'' +
                ", bufferedWriter=" + bufferedWriter +
                ", closed=" + closed +
                ", nextRollingTime=" + nextRollingTime +
                '}';
    }
}
