package cn.myperf4j.base.metric;

import cn.myperf4j.base.MethodTag;

/**
 * Created by LinShunkang on 2018/3/11
 */
public final class MethodMetrics extends Metrics {

    private static final long serialVersionUID = 7715008537772415621L;

    private static final int TP_50_IDX = 0;
    private static final int TP_90_IDX = 1;
    private static final int TP_95_IDX = 2;
    private static final int TP_99_IDX = 3;
    private static final int TP_999_IDX = 4;
    private static final int TP_9999_IDX = 5;
    private static final int TP_100_IDX = 6;

    //注意：tpArr和TOP_PERCENTILE_ARR的大小请保持一致！！！！
    private static final double[] TOP_PERCENTILE_ARR = {0.5D, 0.9D, 0.95D, 0.99D, 0.999D, 0.9999D, 1.0D};

    //注意：tpArr和TOP_PERCENTILE_ARR的大小请保持一致！！！！
    private final int[] tpArr = {-1, -1, -1, -1, -1, -1, -1};

    private final MethodTag methodTag;

    private final int methodTagId;

    private final long startMillTime;

    private final long stopMillTime;

    private int minTime = -1; //ms

    private double avgTime = -1.0D; //ms

    private int maxTime = -1; //ms

    private double stdDev; //Standard Deviation

    private long totalCount = -1L;

    private long totalTime = -1L;

    private double totalTimePercent;

    private MethodMetrics(MethodTag methodTag, int methodTagId, long startMillTime, long stopMillTime) {
        this.methodTag = methodTag;
        this.methodTagId = methodTagId;
        this.startMillTime = startMillTime;
        this.stopMillTime = stopMillTime;
    }

    public MethodTag getMethodTag() {
        return methodTag;
    }

    public int getMethodTagId() {
        return methodTagId;
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public double getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(double avgTime) {
        this.avgTime = avgTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public double getTotalTimePercent() {
        return totalTimePercent;
    }

    public void setTotalTimePercent(double totalTimePercent) {
        this.totalTimePercent = totalTimePercent;
    }

    public int getTP50() {
        return tpArr[TP_50_IDX];
    }

    public int getTP90() {
        return tpArr[TP_90_IDX];
    }

    public int getTP95() {
        return tpArr[TP_95_IDX];
    }

    public int getTP99() {
        return tpArr[TP_99_IDX];
    }

    public int getTP999() {
        return tpArr[TP_999_IDX];
    }

    public int getTP9999() {
        return tpArr[TP_9999_IDX];
    }

    public int getTP100() {
        return tpArr[TP_100_IDX];
    }

    public int[] getTpArr() {
        return tpArr;
    }

    public int getRPS() {
        long seconds = (stopMillTime - startMillTime) / 1000;
        seconds = seconds <= 0L ? 1 : seconds;
        return (int) (totalCount / seconds);
    }

    @Override
    public String toString() {
        return "MethodMetrics{" +
                "methodTag=" + methodTag +
                ", RPS=" + getRPS() +
                ", TP50=" + getTP50() +
                ", TP90=" + getTP90() +
                ", TP95=" + getTP95() +
                ", TP99=" + getTP99() +
                ", TP999=" + getTP999() +
                ", TP9999=" + getTP9999() +
                ", TP100=" + getTP100() +
                ", minTime=" + minTime +
                ", avgTime=" + avgTime +
                ", maxTime=" + maxTime +
                ", stdDev=" + stdDev +
                ", totalCount=" + totalCount +
                ", totalTime=" + totalTime +
                ", totalTimePercent=" + totalTimePercent +
                '}';
    }

    public static MethodMetrics getInstance(MethodTag methodTag,
                                            int methodTagId,
                                            long startMillTime,
                                            long stopMillTime) {
        return new MethodMetrics(methodTag, methodTagId, startMillTime, stopMillTime);
    }

    public static double[] getPercentiles() {
        return TOP_PERCENTILE_ARR;
    }
}
