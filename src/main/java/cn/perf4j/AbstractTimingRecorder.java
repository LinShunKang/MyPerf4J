package cn.perf4j;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/13
 */
public abstract class AbstractTimingRecorder {

    private String api;

    private long startMilliTime;

    private long stopMilliTime;

    private long milliTimeSlice;

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

    public long getMilliTimeSlice() {
        return milliTimeSlice;
    }

    public void setMilliTimeSlice(long milliTimeSlice) {
        this.milliTimeSlice = milliTimeSlice;
    }

    public abstract void recordTime(long startNanoTime, long endNanoTime);

    public abstract List<TimingRecord> getSortedTimingRecords();

    public abstract void resetRecord();

}
