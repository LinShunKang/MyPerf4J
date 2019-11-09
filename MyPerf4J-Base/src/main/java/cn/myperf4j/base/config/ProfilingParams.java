package cn.myperf4j.base.config;

/**
 * Created by LinShunkang on 2018/4/28
 */
public class ProfilingParams {

    private int mostTimeThreshold;//UNIT: ms

    private int outThresholdCount;

    private ProfilingParams(int mostTimeThreshold, int outThresholdCount) {
        this.mostTimeThreshold = mostTimeThreshold;
        this.outThresholdCount = outThresholdCount;
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

    public static ProfilingParams of(int mostTimeThreshold, int outThresholdCount) {
        return new ProfilingParams(mostTimeThreshold, outThresholdCount);
    }
}
