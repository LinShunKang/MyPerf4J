package cn.perf4j;

import java.util.Date;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class PerfStatistic {

    private static final int TP_50_IDX = 0;
    private static final int TP_90_IDX = 1;
    private static final int TP_95_IDX = 2;
    private static final int TP_99_IDX = 3;
    private static final int TP_999_IDX = 4;
    private static final int TP_9999_IDX = 5;
    private static final int TP_100_IDX = 6;

    private static final double[] TOP_PERCENTILE_ARR = {0.5D, 0.9D, 0.95D, 0.99D, 0.999D, 0.9999D, 1.0D};

    private String api;

    private final int[] tpArr = {-1, -1, -1, -1, -1, -1, -1};

    private int minTime = -1;//ms

    private int maxTime = -1;//ms

    private int totalCount = -1;//ms

    private long timeslice = -1;//ms

    private long startMillTime = -1L;

    private long stopMillTime = -1L;

    private PerfStatistic(String api) {
        this.api = api;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getTimeslice() {
        return timeslice;
    }

    public void setTimeslice(long timeslice) {
        this.timeslice = timeslice;
    }

    public int getTp50() {
        return tpArr[TP_50_IDX];
    }

    public void setTp50(int tp50) {
        this.tpArr[TP_50_IDX] = tp50;
    }

    public int getTp90() {
        return tpArr[TP_90_IDX];
    }

    public void setTp90(int tp90) {
        this.tpArr[TP_90_IDX] = tp90;
    }

    public int getTp95() {
        return tpArr[TP_95_IDX];
    }

    public void setTp95(int tp95) {
        this.tpArr[TP_95_IDX] = tp95;
    }

    public int getTp99() {
        return tpArr[TP_99_IDX];
    }

    public void setTp99(int tp99) {
        this.tpArr[TP_99_IDX] = tp99;
    }

    public int getTp999() {
        return tpArr[TP_999_IDX];
    }

    public void setTp999(int tp999) {
        this.tpArr[TP_999_IDX] = tp999;
    }

    public int getTp9999() {
        return tpArr[TP_9999_IDX];
    }

    public void setTp9999(int tp9999) {
        this.tpArr[TP_9999_IDX] = tp9999;
    }

    public int getTp100() {
        return tpArr[TP_100_IDX];
    }

    public void setTp100(int tp100) {
        this.tpArr[TP_100_IDX] = tp100;
    }

    public int[] getTpArr() {
        return tpArr;
    }

    public double[] getPercentiles() {
        return TOP_PERCENTILE_ARR;
    }

    public long getStartMillTime() {
        return startMillTime;
    }

    public void setStartMillTime(long startMillTime) {
        this.startMillTime = startMillTime;
    }

    public long getStopMillTime() {
        return stopMillTime;
    }

    public void setStopMillTime(long stopMillTime) {
        this.stopMillTime = stopMillTime;
    }

    public static PerfStatistic getInstance(String api) {
        return new PerfStatistic(api);
    }

    @Override
    public String toString() {
        return "PerfStatistic{" +
                "api=" + api +
                ", startMillTime=" + new Date(startMillTime) +
                ", stopMillTime=" + new Date(stopMillTime) +
                ", tp50=" + getTp50() +
                ", tp90=" + getTp90() +
                ", tp95=" + getTp95() +
                ", tp99=" + getTp99() +
                ", tp999=" + getTp999() +
                ", tp9999=" + getTp999() +
                ", tp100=" + getTp100() +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", totalCount=" + totalCount +
                ", timeslice=" + timeslice +
                '}';
    }
}
