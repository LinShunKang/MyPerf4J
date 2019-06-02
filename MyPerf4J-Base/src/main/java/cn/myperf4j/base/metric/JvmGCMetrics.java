package cn.myperf4j.base.metric;

import java.lang.management.GarbageCollectorMXBean;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JvmGCMetrics extends Metrics {

    private static final long serialVersionUID = -233095689152915892L;

    private final String gcName;

    private final long collectCount;

    private final long collectTime;

    public JvmGCMetrics(String gcName, long collectCount, long collectTime) {
        this.gcName = gcName;
        this.collectCount = collectCount;
        this.collectTime = collectTime;
    }

    public JvmGCMetrics(GarbageCollectorMXBean bean) {
        this(bean.getName(), bean.getCollectionCount(), bean.getCollectionTime());
    }

    public String getGcName() {
        return gcName;
    }

    public long getCollectCount() {
        return collectCount;
    }

    public long getCollectTime() {
        return collectTime;
    }

    @Override
    public String toString() {
        return "JvmGCMetrics{" +
                "gcName='" + gcName + '\'' +
                ", collectCount=" + collectCount +
                ", collectTime=" + collectTime +
                "} " + super.toString();
    }

}
