package cn.myperf4j.base.metric;

import cn.myperf4j.base.util.NumUtils;

/**
 * Created by LinShunkang on 2019/11/03
 */
public class JvmFileDescriptorMetrics extends Metrics {

    private static final long serialVersionUID = -4979694928407737915L;

    private final long openCount;

    private final long maxCount;

    public JvmFileDescriptorMetrics(long openCount, long maxCount) {
        this.openCount = openCount;
        this.maxCount = maxCount;
    }

    public long getOpenCount() {
        return openCount;
    }

    public long getMaxCount() {
        return maxCount;
    }

    public double getOpenPercent() {
        return NumUtils.getPercent(openCount, maxCount);
    }

    @Override
    public String toString() {
        return "JvmFileDescriptorMetrics{" +
                "curCount=" + openCount +
                ", maxCount=" + maxCount +
                "} " + super.toString();
    }
}
