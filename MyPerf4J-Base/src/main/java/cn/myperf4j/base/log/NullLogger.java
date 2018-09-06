package cn.myperf4j.base.log;

public class NullLogger implements ILogger {

    NullLogger() {
        //empty`
    }

    @Override
    public void log(String msg) {
        //discard
    }

    @Override
    public void logAndFlush(String msg) {
        //discard
    }

    @Override
    public void flushLog() {
        //ignore
    }

    @Override
    public void preCloseLog() {
        //ignore
    }

    @Override
    public void closeLog() {
        //ignore
    }
}
