package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.collections.SetUtils;

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
 * -XX:+UseZGC
 */
public final class JvmGcCollector {

    private static final Set<String> YOUNG_GC_SET = SetUtils.of(
            "Copy",
            "ParNew",
            "PS Scavenge",
            "G1 Young Generation");

    private static final Set<String> OLD_GC_SET = SetUtils.of(
            "MarkSweepCompact",
            "PS MarkSweep",
            "ConcurrentMarkSweep",
            "G1 Old Generation");

    private static volatile long lastYoungGcTime;

    private static volatile long lastYoungGcCount;

    private static volatile long lastOldGcTime;

    private static volatile long lastOldGcCount;

    private static volatile long lastZGcTime;

    private static volatile long lastZGcCount;

    private static volatile long lastZGcCyclesTime;

    private static volatile long lastZGcCyclesCount;

    private static volatile long lastZGcPausesTime;

    private static volatile long lastZGcPausesCount;

    private JvmGcCollector() {
        //empty
    }

    public static JvmGcMetrics collectGcMetrics() {
        long youngGcCount = 0L, youngGcTime = 0L;
        long oldGcCount = 0L, oldGcTime = 0L;
        long zGcCount = 0L, zGcTime = 0L;
        long zGcCyclesCount = 0L, zGcCyclesTime = 0L;
        long zGcPausesCount = 0L, zGcPausesTime = 0L;
        final List<GarbageCollectorMXBean> gcMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        for (int i = 0, size = gcMXBeanList.size(); i < size; i++) {
            final GarbageCollectorMXBean gcMxBean = gcMXBeanList.get(i);
            final String gcName = gcMxBean.getName();
            if (YOUNG_GC_SET.contains(gcName)) {
                youngGcTime += gcMxBean.getCollectionTime();
                youngGcCount += gcMxBean.getCollectionCount();
            } else if (OLD_GC_SET.contains(gcName)) {
                oldGcTime += gcMxBean.getCollectionTime();
                oldGcCount += gcMxBean.getCollectionCount();
            } else if ("ZGC".equals(gcName)) {
                zGcTime += gcMxBean.getCollectionTime();
                zGcCount += gcMxBean.getCollectionCount();
            } else if ("ZGC Cycles".equals(gcName)) {
                zGcCyclesTime += gcMxBean.getCollectionTime();
                zGcCyclesCount += gcMxBean.getCollectionCount();
            } else if ("ZGC Pauses".equals(gcName)) {
                zGcPausesTime += gcMxBean.getCollectionTime();
                zGcPausesCount += gcMxBean.getCollectionCount();
            } else {
                Logger.warn("Unknown GC: " + gcName);
            }
        }

        final JvmGcMetrics jvmGcMetrics = new JvmGcMetrics(
                youngGcCount - lastYoungGcCount,
                youngGcTime - lastYoungGcTime,
                oldGcCount - lastOldGcCount,
                oldGcTime - lastOldGcTime,
                zGcCount - lastZGcCount,
                zGcTime - lastZGcTime,
                zGcCyclesCount - lastZGcCyclesCount,
                zGcCyclesTime - lastZGcCyclesTime,
                zGcPausesCount - lastZGcPausesCount,
                zGcPausesTime - lastZGcPausesTime);

        lastYoungGcCount = youngGcCount;
        lastYoungGcTime = youngGcTime;
        lastOldGcCount = oldGcCount;
        lastOldGcTime = oldGcTime;
        lastZGcCount = zGcCount;
        lastZGcTime = zGcTime;
        lastZGcCyclesCount = zGcCyclesCount;
        lastZGcCyclesTime = zGcCyclesTime;
        lastZGcPausesCount = zGcPausesCount;
        lastZGcPausesTime = zGcPausesTime;
        return jvmGcMetrics;
    }
}
