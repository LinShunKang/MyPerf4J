package cn.myperf4j.base.metric;

import java.lang.management.BufferPoolMXBean;

/**
 * Created by LinShunkang on 2018/11/1
 */
public class JvmBufferPoolMetrics extends Metrics {

    private static final long serialVersionUID = 1308517280962399791L;

    private final String name;

    private final long count;

    private final long memoryUsed;

    private final long memoryCapacity;

    public JvmBufferPoolMetrics(BufferPoolMXBean mxBean) {
        this.name = mxBean.getName();
        this.count = mxBean.getCount();
        this.memoryUsed = mxBean.getMemoryUsed();
        this.memoryCapacity = mxBean.getTotalCapacity();
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public long getMemoryCapacity() {
        return memoryCapacity;
    }

    @Override
    public String toString() {
        return "JvmBufferPoolMetrics{" +
                "name='" + name + '\'' +
                ", count=" + count +
                ", memoryUsed=" + memoryUsed +
                ", memoryCapacity=" + memoryCapacity +
                '}';
    }
}
