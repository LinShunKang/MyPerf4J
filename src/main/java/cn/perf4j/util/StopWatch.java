package cn.perf4j.util;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class StopWatch {

    private static final long NANOS_IN_A_MILLI = 1000000L;

    private long startNanoTime;

    private long elapsedMillTime;

    private String tag;

    private String message;

    public StopWatch() {
        this("", null);
    }


    public StopWatch(String tag) {
        this(tag, null);
    }


    public StopWatch(String tag, String message) {
        this(System.nanoTime(), -1L, tag, message);
    }

    public StopWatch(long startNanoTime, long elapsedMillTime, String tag, String message) {
        this.startNanoTime = startNanoTime;
        this.elapsedMillTime = elapsedMillTime;
        this.tag = tag;
        this.message = message;
    }

    public long getStartNanoTime() {
        return startNanoTime;
    }

    public long getElapsedMillTime() {
        return (elapsedMillTime == -1L) ?
                (System.nanoTime() - startNanoTime) / NANOS_IN_A_MILLI :
                elapsedMillTime;
    }

    public String getTag() {
        return tag;
    }

    public StopWatch setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public StopWatch setMessage(String message) {
        this.message = message;
        return this;
    }

    public void start() {
        startNanoTime = System.nanoTime();
        elapsedMillTime = -1L;
    }

    public void start(String tag) {
        start();
        this.tag = tag;
    }

    public void start(String tag, String message) {
        start();
        this.tag = tag;
        this.message = message;
    }

    public String stop() {
        elapsedMillTime = (System.nanoTime() - startNanoTime) / NANOS_IN_A_MILLI;
        return this.toString();
    }

    public String stop(String tag) {
        this.tag = tag;
        return stop();
    }

    public String stop(String tag, String message) {
        this.tag = tag;
        this.message = message;
        return stop();
    }

    public String lap(String tag) {
        String retVal = stop(tag);
        start();
        return retVal;
    }

    public String lap(String tag, String message) {
        String retVal = stop(tag, message);
        start();
        return retVal;
    }

    public String toString() {
        String message = getMessage();
//        return "start[" + getStartNanoTime() + "] " +
        return "time[" + getElapsedMillTime() + "ms] " +
                "tag[" + getTag() + ((message == null) ? "]" : "] message[" + message + "]");
    }

}
