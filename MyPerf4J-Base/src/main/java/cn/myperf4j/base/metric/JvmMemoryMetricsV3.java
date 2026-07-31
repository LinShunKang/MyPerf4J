package cn.myperf4j.base.metric;

import cn.myperf4j.base.util.NumUtils;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class JvmMemoryMetricsV3 extends Metrics {

    private static final long serialVersionUID = -1501873958253505089L;

    private final String poolName;

    private final long init; //单位：KB

    private final long used; //单位：KB

    private final long committed; //单位：KB

    private final long max; //单位：KB

    public JvmMemoryMetricsV3(String poolName, long init, long used, long committed, long max) {
        this.poolName = poolName;
        this.init = init;
        this.used = used;
        this.committed = committed;
        this.max = max;
    }

    public String getPoolName() {
        return poolName;
    }

    public long getInit() {
        return init;
    }

    public long getUsed() {
        return used;
    }

    public double getUsedPercent() {
        return NumUtils.getPercent(used, max);
    }

    public long getCommitted() {
        return committed;
    }

    public long getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "JvmMemoryMetricsV3{" +
                "poolName='" + poolName + '\'' +
                ", init=" + init +
                ", used=" + used +
                ", committed=" + committed +
                ", max=" + max +
                '}';
    }
}
