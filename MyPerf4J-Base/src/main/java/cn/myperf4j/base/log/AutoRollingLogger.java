package cn.myperf4j.base.log;

import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.file.AutoRollingFileWriter;
import cn.myperf4j.base.util.file.DailyRollingFileWriter;
import cn.myperf4j.base.util.file.HourlyRollingFileWriter;
import cn.myperf4j.base.util.file.MinutelyRollingFileWriter;

public class AutoRollingLogger implements ILogger {

    private final AutoRollingFileWriter writer;

    AutoRollingLogger(String logFile, String rollingTimeUnit, int reserveFileCount) {
        switch (rollingTimeUnit.toUpperCase()) {
            case PropertyValues.LOG_ROLLING_TIME_HOURLY:
                this.writer = new HourlyRollingFileWriter(logFile, reserveFileCount);
                break;
            case PropertyValues.LOG_ROLLING_TIME_MINUTELY:
                this.writer = new MinutelyRollingFileWriter(logFile, reserveFileCount);
                break;
            default:
                this.writer = new DailyRollingFileWriter(logFile, reserveFileCount);
        }
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
        writer.closeFile(true);
    }

    @Override
    protected void finalize() {
        writer.closeFile(true);
    }
}
