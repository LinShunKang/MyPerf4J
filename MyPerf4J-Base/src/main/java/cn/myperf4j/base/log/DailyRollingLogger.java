package cn.myperf4j.base.log;

import cn.myperf4j.base.util.DailyRollingFileWriter;

public class DailyRollingLogger implements ILogger {

    private final DailyRollingFileWriter writer;

    DailyRollingLogger(String logFile) {
        this.writer = new DailyRollingFileWriter(logFile);
    }

    @Override
    public void log(String msg) {
        writer.write(msg + '\n');
    }

    @Override
    public void logAndFlush(String msg) {
        writer.writeAndFlush(msg + '\n');
    }

    @Override
    public void flushLog() {
        writer.flush();
    }

    @Override
    public void preCloseLog() {
        writer.preCloseFile();
    }

    @Override
    public void closeLog() {
        writer.closeFile();
    }

    @Override
    protected void finalize() {
        writer.closeFile();
    }
}
