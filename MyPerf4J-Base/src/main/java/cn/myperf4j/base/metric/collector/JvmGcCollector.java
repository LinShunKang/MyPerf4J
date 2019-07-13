package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.SetUtils;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;

/**
 * Created by LinShunkang on 2019/06/23
 * <p>
 * -XX:+UseParallelGC -XX:+UseParallelOldGC
 * -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
 * -XX:+UseSerialGC
 * -XX:+UseG1GC
 */
public final class JvmGcCollector {

    private static final Set<String> YOUNG_GC_SET = SetUtils.of("Copy", "ParNew", "PS Scavenge", "G1 Young Generation");

    private static final Set<String> OLD_GC_SET = SetUtils.of("MarkSweepCompact", "PS MarkSweep", "ConcurrentMarkSweep", "G1 Old Generation");

    private static volatile long lastYoungGcTime = 0L;

    private static volatile long lastYoungGcCount = 0L;

    private static volatile long lastOldGcTime = 0L;

    private static volatile long lastOldGcCount = 0L;

    public static JvmGcMetrics collectGcMetrics() {
        long youngGcCount = 0L;
        long youngGcTime = 0L;
        long oldGCount = 0L;
        long oldGcTime = 0L;

        List<GarbageCollectorMXBean> gcMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        for (int i = 0; i < gcMXBeanList.size(); i++) {
            GarbageCollectorMXBean gcMxBean = gcMXBeanList.get(i);
            String gcName = gcMxBean.getName();
            if (YOUNG_GC_SET.contains(gcName)) {
                youngGcTime += gcMxBean.getCollectionTime();
                youngGcCount += gcMxBean.getCollectionCount();
            } else if (OLD_GC_SET.contains(gcName)) {
                oldGcTime += gcMxBean.getCollectionTime();
                oldGCount += gcMxBean.getCollectionCount();
            } else {
                Logger.warn("Unknown GC: " + gcName);
            }
        }

        JvmGcMetrics jvmGcMetrics = new JvmGcMetrics(
                youngGcCount - lastYoungGcCount,
                youngGcTime - lastYoungGcTime,
                oldGCount - lastOldGcCount,
                oldGcTime - lastOldGcTime);

        lastYoungGcCount = youngGcCount;
        lastYoungGcTime = youngGcTime;
        lastOldGcCount = oldGCount;
        lastOldGcTime = oldGcTime;

        return jvmGcMetrics;
    }

    public static void main(String[] args) {
        List<GarbageCollectorMXBean> gcMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        for (final GarbageCollectorMXBean gcMxBean : gcMXBeanList) {
            System.out.println("Name=" + gcMxBean.getName() + ", gcTime=" + gcMxBean.getCollectionTime() + ", gcCount=" + gcMxBean.getCollectionCount());
        }
    }
}
