package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmMemoryMetrics;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * Created by LinShunkang on 2019/06/23
 */
public final class JvmMemoryCollector {

    public static JvmMemoryMetrics collectMemoryMetrics() {
        long oldGenUsed = 0L, oldGenMax = 0L;
        long permGenUsed = 0L, permGenMax = 0L;
        long edenUsed = 0L, edenMax = 0L;
        long survivorUsed = 0L, survivorMax = 0L;
        long metaspaceUsed = 0L, metaSpaceMax = 0L;
        long codeCacheUsed = 0L, codeCacheMax = 0L;

        List<MemoryPoolMXBean> mxBeanList = ManagementFactory.getMemoryPoolMXBeans();
        for (int i = 0; i < mxBeanList.size(); i++) {
            MemoryPoolMXBean memoryPool = mxBeanList.get(i);
            MemoryUsage usage = memoryPool.getUsage();
            String poolName = memoryPool.getName();
            if (poolName.endsWith("Perm Gen")) {
                permGenUsed = usage.getUsed() >> 10;
                permGenMax = usage.getMax() >> 10;
            } else if (poolName.endsWith("Metaspace")) {
                metaspaceUsed = usage.getUsed() >> 10;
                metaSpaceMax = usage.getMax() >> 10;
            } else if (poolName.endsWith("Code Cache")) {
                codeCacheUsed = usage.getUsed() >> 10;
                codeCacheMax = usage.getMax() >> 10;
            } else if (poolName.endsWith("Old Gen")) {
                oldGenUsed = usage.getUsed() >> 10;
                oldGenMax = usage.getMax() >> 10;
            } else if (poolName.endsWith("Eden Space")) {
                edenUsed = usage.getUsed() >> 10;
                edenMax = usage.getMax() >> 10;
            } else if (poolName.endsWith("Survivor Space")) {
                survivorUsed = usage.getUsed() >> 10;
                survivorMax = usage.getMax() >> 10;
            }
        }

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMem = memoryMXBean.getNonHeapMemoryUsage();
        long nonHeapUsed = nonHeapMem.getUsed() >> 10;
        long nonHeapMax = nonHeapMem.getMax() >> 10;

        MemoryUsage heapMem = memoryMXBean.getHeapMemoryUsage();
        long heapUsed = heapMem.getUsed() >> 10;
        long heapMax = heapMem.getMax() >> 10;

        return new JvmMemoryMetrics(
                heapUsed, heapMax,
                nonHeapUsed, nonHeapMax,
                permGenUsed, permGenMax,
                metaspaceUsed, metaSpaceMax,
                codeCacheUsed, codeCacheMax,
                oldGenUsed, oldGenMax,
                edenUsed, edenMax,
                survivorUsed, survivorMax);
    }

}
