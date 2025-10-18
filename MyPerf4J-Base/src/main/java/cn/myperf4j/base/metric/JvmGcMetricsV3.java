package cn.myperf4j.base.metric;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class JvmGcMetricsV3 extends Metrics {

    private static final long serialVersionUID = -233095689152915892L;

    private final String gcName;

    private final long gcCount;

    private final long gcTime;

    private final double avgGcTime;

    public JvmGcMetricsV3(String gcName, long gcCount, long gcTime) {
        this.gcName = gcName;
        this.gcCount = gcCount;
        this.gcTime = gcTime;
        this.avgGcTime = gcCount > 0L ? ((double) gcTime) / gcCount : 0D;
    }

    public String getGcName() {
        return gcName;
    }

    public long getGcCount() {
        return gcCount;
    }

    public long getGcTime() {
        return gcTime;
    }

    public double getAvgGcTime() {
        return avgGcTime;
    }

    @Override
    public String toString() {
        return "JvmGcMetricsV3{" +
                "gcName='" + gcName + '\'' +
                ", gcCount=" + gcCount +
                ", gcTime=" + gcTime +
                ", avgGcTime=" + avgGcTime +
                "} " + super.toString();
    }
}
