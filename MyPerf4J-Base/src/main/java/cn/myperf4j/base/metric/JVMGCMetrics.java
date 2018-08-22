package cn.myperf4j.base.metric;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JVMGCMetrics extends Metrics {

    private static final long serialVersionUID = -233095689152915892L;

    private String gcName;

    private long collectCount;

    private long collectTime;

    public JVMGCMetrics(String gcName, long collectCount, long collectTime) {
        this.gcName = gcName;
        this.collectCount = collectCount;
        this.collectTime = collectTime;
    }

    public JVMGCMetrics(GarbageCollectorMXBean bean) {
        this(bean.getName(), bean.getCollectionCount(), bean.getCollectionTime());
    }

    public String getGcName() {
        return gcName;
    }

    public void setGcName(String gcName) {
        this.gcName = gcName;
    }

    public long getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(long collectCount) {
        this.collectCount = collectCount;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    @Override
    public String toString() {
        return "JVMGCMetrics{" +
                "gcName='" + gcName + '\'' +
                ", collectCount=" + collectCount +
                ", collectTime=" + collectTime +
                "} " + super.toString();
    }

    public static void main(String[] args) {
        List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean bean : garbageCollectorMxBeans) {
            System.out.println(bean.getName() + ": " + bean.getCollectionCount() + ", " + bean.getCollectionTime());
        }
    }
}
