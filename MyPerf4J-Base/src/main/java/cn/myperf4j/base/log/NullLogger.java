package cn.myperf4j.base.log;

public class NullLogger implements ILogger {

    @Override
    public void log(String msg) {
        //discard
    }
}
