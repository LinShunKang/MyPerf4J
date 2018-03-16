package cn.perf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class PerfStats {

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int TP_50_IDX = 0;
    private static final int TP_90_IDX = 1;
    private static final int TP_95_IDX = 2;
    private static final int TP_99_IDX = 3;
    private static final int TP_999_IDX = 4;
    private static final int TP_9999_IDX = 5;
    private static final int TP_100_IDX = 6;

    private static final double[] TOP_PERCENTILE_ARR = {0.5D, 0.9D, 0.95D, 0.99D, 0.999D, 0.9999D, 1.0D};

    private String api;

    private final int[] TPArr = {-1, -1, -1, -1, -1, -1, -1};

    private int minTime = -1;//ms

    private int maxTime = -1;//ms

    private int totalCount = -1;//ms

    private long startMillTime = -1L;

    private long stopMillTime = -1L;

    private PerfStats(String api) {
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

    public int getTP50() {
        return TPArr[TP_50_IDX];
    }

    public void setTP50(int TP50) {
        this.TPArr[TP_50_IDX] = TP50;
    }

    public int getTP90() {
        return TPArr[TP_90_IDX];
    }

    public void setTP90(int TP90) {
        this.TPArr[TP_90_IDX] = TP90;
    }

    public int getTP95() {
        return TPArr[TP_95_IDX];
    }

    public void setTP95(int TP95) {
        this.TPArr[TP_95_IDX] = TP95;
    }

    public int getTP99() {
        return TPArr[TP_99_IDX];
    }

    public void setTP99(int TP99) {
        this.TPArr[TP_99_IDX] = TP99;
    }

    public int getTP999() {
        return TPArr[TP_999_IDX];
    }

    public void setTP999(int TP999) {
        this.TPArr[TP_999_IDX] = TP999;
    }

    public int getTP9999() {
        return TPArr[TP_9999_IDX];
    }

    public void setTP9999(int TP9999) {
        this.TPArr[TP_9999_IDX] = TP9999;
    }

    public int getTP100() {
        return TPArr[TP_100_IDX];
    }

    public void setTP100(int TP100) {
        this.TPArr[TP_100_IDX] = TP100;
    }

    public int[] getTPArr() {
        return TPArr;
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

    public int getRPS() {
        long mills = (stopMillTime - startMillTime) / 1000 + 1;
        return (int) (totalCount / mills);
    }


    @Override
    public String toString() {
        return "PerfStats{" +
                "api=" + api +
                ", [" + dateFormat.format(new Date(startMillTime)) + ", " + dateFormat.format(new Date(stopMillTime)) + "]" +
                ", RPS=" + getRPS() +
                ", TP50=" + getTP50() +
                ", TP90=" + getTP90() +
                ", TP95=" + getTP95() +
                ", TP99=" + getTP99() +
                ", TP999=" + getTP999() +
                ", TP9999=" + getTP9999() +
                ", TP100=" + getTP100() +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", totalCount=" + totalCount +
                '}';
    }

    public static PerfStats getInstance(String api) {
        return new PerfStats(api);
    }


    public static double[] getPercentiles() {
        return TOP_PERCENTILE_ARR;
    }

}
