package cn.myperf4j.base.log;

/**
 * Created by LinShunkang on 2018/10/29
 */
public class StdoutLogger implements ILogger {

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }

    @Override
    public void logAndFlush(String msg) {
        log(msg);
    }

    @Override
    public void flushLog() {
        //empty
    }

    @Override
    public void preCloseLog() {
        //empty
    }

    @Override
    public void closeLog() {
        //empty
    }
}
