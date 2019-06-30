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
public class JvmMemoryCollector {

    public static JvmMemoryMetrics collectMemoryMetrics() {
        long oldGenUsed = 0L, oldGenMax = 0L;
        long permGenUsed = 0L, permGenMax = 0L;
        long edenUsed = 0L, edenMax = 0L;
        long survivorUsed = 0L, survivorMax = 0L;
        long metaspaceUsed = 0L, metaSpaceMax = 0L;
        long codeCacheUsed = 0L, codeCacheMax = 0L;

        List<MemoryPoolMXBean> memPoolMXBeanList = ManagementFactory.getMemoryPoolMXBeans();
        for (int i = 0; i < memPoolMXBeanList.size(); i++) {
            MemoryPoolMXBean memoryPool = memPoolMXBeanList.get(i);
            MemoryUsage usage = memoryPool.getUsage();
            String poolName = memoryPool.getName();
            if (poolName.endsWith("Perm Gen")) {
                permGenUsed = usage.getUsed() / 1024;
                permGenMax = usage.getMax() / 1024;
            } else if (poolName.endsWith("Metaspace")) {
                metaspaceUsed = usage.getUsed() / 1024;
                metaSpaceMax = usage.getMax() / 1024;
            } else if (poolName.endsWith("Code Cache")) {
                codeCacheUsed = usage.getUsed() / 1024;
                codeCacheMax = usage.getMax() / 1024;
            } else if (poolName.endsWith("Old Gen")) {
                oldGenUsed = usage.getUsed() / 1024;
                oldGenMax = usage.getMax() / 1024;
            } else if (poolName.endsWith("Eden Space")) {
                edenUsed = usage.getUsed() / 1024;
                edenMax = usage.getMax() / 1024;
            } else if (poolName.endsWith("Survivor Space")) {
                survivorUsed = usage.getUsed() / 1024;
                survivorMax = usage.getMax() / 1024;
            }
        }

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMem = memoryMXBean.getNonHeapMemoryUsage();
        long nonHeapUsed = nonHeapMem.getUsed() / 1024;
        long nonHeapMax = nonHeapMem.getMax() / 1024;

        MemoryUsage heapMem = memoryMXBean.getHeapMemoryUsage();
        long heapUsed = heapMem.getUsed() / 1024;
        long heapMax = heapMem.getMax() / 1024;

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
