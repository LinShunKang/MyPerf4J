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

    private static final Set<String> Z_GC_SET = SetUtils.of("ZGC");

    private static volatile long lastYoungGcTime;

    private static volatile long lastYoungGcCount;

    private static volatile long lastOldGcTime;

    private static volatile long lastOldGcCount;

    private static volatile long lastZGcTime;

    private static volatile long lastZGcCount;

    private JvmGcCollector() {
        //empty
    }

    public static JvmGcMetrics collectGcMetrics() {
        long youngGcCount = 0L;
        long youngGcTime = 0L;
        long oldGcCount = 0L;
        long oldGcTime = 0L;
        long zGcCount = 0L;
        long zGcTime = 0L;

        List<GarbageCollectorMXBean> gcMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        for (int i = 0; i < gcMXBeanList.size(); i++) {
            GarbageCollectorMXBean gcMxBean = gcMXBeanList.get(i);
            String gcName = gcMxBean.getName();
            if (YOUNG_GC_SET.contains(gcName)) {
                youngGcTime += gcMxBean.getCollectionTime();
                youngGcCount += gcMxBean.getCollectionCount();
            } else if (OLD_GC_SET.contains(gcName)) {
                oldGcTime += gcMxBean.getCollectionTime();
                oldGcCount += gcMxBean.getCollectionCount();
            } else if (Z_GC_SET.contains(gcName)) {
                zGcTime += gcMxBean.getCollectionTime();
                zGcCount += gcMxBean.getCollectionCount();
            } else {
                Logger.warn("Unknown GC: " + gcName);
            }
        }

        JvmGcMetrics jvmGcMetrics = new JvmGcMetrics(
                youngGcCount - lastYoungGcCount,
                youngGcTime - lastYoungGcTime,
                oldGcCount - lastOldGcCount,
                oldGcTime - lastOldGcTime,
                zGcCount - lastZGcCount,
                zGcTime - lastZGcTime);

        lastYoungGcCount = youngGcCount;
        lastYoungGcTime = youngGcTime;
        lastOldGcCount = oldGcCount;
        lastOldGcTime = oldGcTime;
        lastZGcCount = zGcCount;
        lastZGcTime = zGcTime;
        return jvmGcMetrics;
    }
}
