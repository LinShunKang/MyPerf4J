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

    private final long zGcTime;

    private final long zGcCount;

    private final double avgZGcTime;

    public JvmGcMetrics(long youngGcCount,
                        long youngGcTime,
                        long fullGcCount,
                        long fullGcTime,
                        long zGcCount,
                        long zGcTime) {
        this.youngGcCount = youngGcCount;
        this.youngGcTime = youngGcTime;
        this.avgYoungGcTime = getAvgTime(youngGcCount, youngGcTime);
        this.fullGcCount = fullGcCount;
        this.fullGcTime = fullGcTime;
        this.zGcCount = zGcCount;
        this.zGcTime = zGcTime;
        this.avgZGcTime = getAvgTime(zGcCount, zGcTime);
    }

    private double getAvgTime(long count, long time) {
        return count > 0L ? ((double) time) / count : 0D;
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

    public long getZGcTime() {
        return zGcTime;
    }

    public long getZGcCount() {
        return zGcCount;
    }

    public double getAvgZGcTime() {
        return avgZGcTime;
    }

    @Override
    public String toString() {
        return "JvmGcMetrics{" +
                "youngGcCount=" + youngGcCount +
                ", youngGcTime=" + youngGcTime +
                ", avgYoungGcTime=" + avgYoungGcTime +
                ", fullGcCount=" + fullGcCount +
                ", fullGcTime=" + fullGcTime +
                ", zGcTime=" + zGcTime +
                ", zGcCount=" + zGcCount +
                ", avgZGcTime=" + avgZGcTime +
                '}';
    }
}
