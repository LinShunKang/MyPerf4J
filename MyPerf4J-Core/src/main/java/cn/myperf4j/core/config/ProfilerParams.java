package cn.myperf4j.core.config;

/**
 * Created by LinShunkang on 2018/4/28
 */
public class ProfilerParams {

    private boolean hasProfiler;//是否有Profiler注解

    private int mostTimeThreshold;//单位:ms

    private int outThresholdCount;

    private ProfilerParams(boolean hasProfiler, int mostTimeThreshold, int outThresholdCount) {
        this.hasProfiler = hasProfiler;
        this.mostTimeThreshold = mostTimeThreshold;
        this.outThresholdCount = outThresholdCount;
    }

    public boolean hasProfiler() {
        return hasProfiler;
    }

    public void setHasProfiler(boolean hasProfiler) {
        this.hasProfiler = hasProfiler;
    }

    public int getMostTimeThreshold() {
        return mostTimeThreshold;
    }

    public void setMostTimeThreshold(int mostTimeThreshold) {
        this.mostTimeThreshold = mostTimeThreshold;
    }

    public int getOutThresholdCount() {
        return outThresholdCount;
    }

    public void setOutThresholdCount(int outThresholdCount) {
        this.outThresholdCount = outThresholdCount;
    }

    public static ProfilerParams of(boolean hasProfiler, int mostTimeThreshold, int outThresholdCount) {
        return new ProfilerParams(hasProfiler, mostTimeThreshold, outThresholdCount);
    }

    public static ProfilerParams of(boolean hasProfiler) {
        return new ProfilerParams(hasProfiler, 1000, 10);
    }

    public static ProfilerParams of(ProfilerParams params) {
        return new ProfilerParams(params.hasProfiler, params.getMostTimeThreshold(), params.getOutThresholdCount());
    }
}
