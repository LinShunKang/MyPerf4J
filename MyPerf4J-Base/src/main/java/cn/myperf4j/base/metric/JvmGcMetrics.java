package cn.myperf4j.base.metric;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JvmGcMetrics extends Metrics {

    private static final long serialVersionUID = -233095689152915892L;

    private final long youngGcCount;

    private final long youngGcTime;

    private final double avgYoungGcTime;

    private final long fullGcCount;

    private final long fullGcTime;

    public JvmGcMetrics(long youngGcCount, long youngGcTime, long fullGcCount, long fullGcTime) {
        this.youngGcCount = youngGcCount;
        this.youngGcTime = youngGcTime;
        this.avgYoungGcTime = youngGcCount > 0L ? ((double) youngGcTime) / youngGcCount : 0D;
        this.fullGcCount = fullGcCount;
        this.fullGcTime = fullGcTime;
    }

    public double getAvgYoungGcTime() {
        return avgYoungGcTime;
    }

    public long getFullGcCount() {
        return fullGcCount;
    }

    public long getFullGcTime() {
        return fullGcTime;
    }

    public long getYoungGcCount() {
        return youngGcCount;
    }

    public long getYoungGcTime() {
        return youngGcTime;
    }

    @Override
    public String toString() {
        return "JvmGcMetrics{" +
                "youngGcCount=" + youngGcCount +
                ", youngGcTime=" + youngGcTime +
                ", avgYoungGcTime=" + avgYoungGcTime +
                ", fullGcCount=" + fullGcCount +
                ", fullGcTime=" + fullGcTime +
                "} " + super.toString();
    }
}
