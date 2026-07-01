package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmMemoryMetricsV3;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LinShunkang on 2019/06/23
 */
public final class JvmMemoryV3Collector {

    private JvmMemoryV3Collector() {
        //empty
    }

    public static List<JvmMemoryMetricsV3> collectMemoryMetrics() {
        final List<MemoryPoolMXBean> mxBeanList = ManagementFactory.getMemoryPoolMXBeans();
        final List<JvmMemoryMetricsV3> result = new ArrayList<>(mxBeanList.size() + 2);
        for (final MemoryPoolMXBean memoryPool : mxBeanList) {
            result.add(generateMetrics(memoryPool.getName(), memoryPool.getUsage()));
        }

        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        result.add(generateMetrics("NonHeap", memoryMXBean.getNonHeapMemoryUsage()));
        result.add(generateMetrics("Heap", memoryMXBean.getHeapMemoryUsage()));
        return result;
    }

    private static JvmMemoryMetricsV3 generateMetrics(String poolName, MemoryUsage usage) {
        return new JvmMemoryMetricsV3(poolName,
                usage.getInit() >> 10,
                usage.getUsed() >> 10,
                usage.getCommitted() >> 10,
                usage.getMax() >> 10);
    }
}
