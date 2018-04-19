package cn.myperf4j.core;

/**
 * Created by LinShunkang on 2018/3/13
 */
public abstract class AbstractRecorder {

    private String tag;

    private long startTime;//ms

    private long stopTime;//ms

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startMilliTime) {
        this.startTime = startMilliTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public abstract void recordTime(long startNanoTime, long endNanoTime);

    /**
     * 为了节省内存的使用，利用int[]作为返回结果
     *
     * @param arr : arr.length为effectiveRecordCount的两倍!!! 其中，第0位存timeCost，第1位存count，第2位存timeCost，第3位存count，以此类推
     */
    public abstract void fillSortedRecords(int[] arr);

    /**
     * 获取有效的记录的个数
     */
    public abstract int getEffectiveCount();

    public abstract void resetRecord();

    public abstract int getOutThresholdCount();

}
