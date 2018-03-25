package cn.perf4j;

/**
 * Created by LinShunkang on 2018/3/13
 */
public abstract class AbstractRecorder {

    private String api;

    private long startMilliTime;

    private long stopMilliTime;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public long getStartMilliTime() {
        return startMilliTime;
    }

    public void setStartMilliTime(long startMilliTime) {
        this.startMilliTime = startMilliTime;
    }

    public long getStopMilliTime() {
        return stopMilliTime;
    }

    public void setStopMilliTime(long stopMilliTime) {
        this.stopMilliTime = stopMilliTime;
    }

    public abstract void recordTime(long startNanoTime, long endNanoTime);

    /**
     * 为了节省内存的使用，利用int[]作为返回结果
     *
     * @return :第0位存timeCost，第1位存count，第2位存timeCost，第3位存count，以此类推
     */
    public abstract int[] getSortedTimingRecords();

    public abstract void resetRecord();

    public abstract int getOutThresholdCount();

}
