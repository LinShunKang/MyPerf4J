package cn.myperf4j.base.util.file;

import cn.myperf4j.base.util.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/9/7
 * 该类参考自TProfiler: https://github.com/alibaba/TProfiler/blob/master/src/main/java/com/taobao/profile/utils/DailyRollingFileWriter.java
 */
public abstract class AutoRollingFileWriter {

    private final String fileName;

    private volatile String rollingFileName;

    private volatile BufferedWriter bufferedWriter;

    private volatile boolean closed;

    private volatile long nextRollingTime;


    public AutoRollingFileWriter(String fileName) {
        Date now = new Date();

        this.fileName = fileName;
        this.closed = false;
        this.nextRollingTime = getNextRollingTime(now);
        this.rollingFileName = formatDateFileName(fileName, now);

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
    }

    abstract long getNextRollingTime(Date now);

    abstract String formatDateFileName(String fileName, Date date);

    abstract boolean isSameEpoch(Date date1, Date date2);

    private void createWriter(File file, boolean append) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean mkdirs = parent.mkdirs();
                Logger.info("AutoRollingFileWriter.createWriter(" + file.getName() + ", " + append + "): mkdirs=" + mkdirs);
            }

            bufferedWriter = new BufferedWriter(new FileWriter(file, append), 64 * 1024);
        } catch (IOException e) {
            Logger.error("AutoRollingFileWriter.createWriter(" + file.getName() + ", " + append + ")", e);
        }
    }

    private void rollingFile(Date now) {
        String datedFilename = formatDateFileName(fileName, now);
        if (rollingFileName.equals(datedFilename)) {
            Logger.info("AutoRollingFileWriter.rollingFile(" + now + "): rollingFile=" + rollingFileName + ", datedFilename=" + datedFilename + " return!!!");
            return;
        }

        closeFile(false);

        File targetFile = new File(rollingFileName);
        if (targetFile.exists()) {
            boolean delete = targetFile.delete();
            Logger.info("AutoRollingFileWriter.rollingFile(" + now + "): rollingFile=" + rollingFileName + ", delete=" + delete);
        }

        File file = new File(fileName);
        boolean rename = file.renameTo(targetFile);
        Logger.info("AutoRollingFileWriter.rollingFile(" + now + "): rename " + fileName + " to " + targetFile.getName() + " " + rename);

        createWriter(new File(fileName), false);
        rollingFileName = datedFilename;
    }

    public void write(String msg) {
        write0(msg);
    }

    private void write0(String msg) {
        try {
            long time = System.currentTimeMillis();
            if (time > nextRollingTime) {
                synchronized (this) {
                    if (time > nextRollingTime) {
                        Date now = new Date();
                        nextRollingTime = getNextRollingTime(now);
                        rollingFile(now);
                    }
                }
            }

            if (bufferedWriter != null && !closed) {
                bufferedWriter.write(msg);
            }
        } catch (Exception e) {
            Logger.error("AutoRollingFileWriter.write0(msg)", e);
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
                bufferedWriter.flush();
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
