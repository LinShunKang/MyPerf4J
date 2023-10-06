package cn.myperf4j.base.config;

/**
 * Created by LinShunkang on 2018/4/28
 */
public final class ProfilingParams {

    private final int mostTimeThreshold; //UNIT: ms

    private final int outThresholdCount;

    private ProfilingParams(int mostTimeThreshold, int outThresholdCount) {
        this.mostTimeThreshold = mostTimeThreshold;
        this.outThresholdCount = outThresholdCount;
    }

    public int mostTimeThreshold() {
        return mostTimeThreshold;
    }

    public int outThresholdCount() {
        return outThresholdCount;
    }

    public static ProfilingParams of(int mostTimeThreshold, int outThresholdCount) {
        return new ProfilingParams(mostTimeThreshold, outThresholdCount);
    }
}
