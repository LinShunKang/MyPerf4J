package cn.perf4j;

import java.util.List;

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

    public abstract List<Record> getSortedTimingRecords();

    public abstract void resetRecord();

}
