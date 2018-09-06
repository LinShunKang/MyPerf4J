package cn.myperf4j.base.log;

public interface ILogger {

    void log(String msg);

    void logAndFlush(String msg);

    void flushLog();

    void preCloseLog();

    void closeLog();

}
