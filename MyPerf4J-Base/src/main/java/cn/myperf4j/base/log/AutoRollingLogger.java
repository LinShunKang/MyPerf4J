package cn.myperf4j.base.log;

import cn.myperf4j.base.constant.PropertyValues.Metrics;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.DailyRollingFileWriter;
import cn.myperf4j.base.file.HourlyRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

public class AutoRollingLogger implements ILogger {

    private final AutoRollingFileWriter writer;

    AutoRollingLogger(String logFile, String rollingTimeUnit, int reserveFileCount) {
        switch (rollingTimeUnit.toUpperCase()) {
            case Metrics.LOG_ROLLING_HOURLY:
                this.writer = new HourlyRollingFileWriter(logFile, reserveFileCount);
                break;
            case Metrics.LOG_ROLLING_MINUTELY:
                this.writer = new MinutelyRollingFileWriter(logFile, reserveFileCount);
                break;
            default:
                this.writer = new DailyRollingFileWriter(logFile, reserveFileCount);
        }
    }

    @Override
    public void log(String msg) {
        writer.write(msg + LINE_SEPARATOR);
    }

    @Override
    public void logAndFlush(String msg) {
        writer.writeAndFlush(msg + LINE_SEPARATOR);
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
    protected void finalize() throws Throwable {
        super.finalize();
        writer.closeFile(true);
    }
}
