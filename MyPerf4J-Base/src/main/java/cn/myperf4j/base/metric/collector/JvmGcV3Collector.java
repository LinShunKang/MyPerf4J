package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmGcMetricsV3;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2024/02/08
 * <p>
 * -XX:+UseParallelGC -XX:+UseParallelOldGC
 * -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
 * -XX:+UseSerialGC
 * -XX:+UseG1GC
 * -XX:+UseZGC [-XX:+ZGenerational]
 */
public final class JvmGcV3Collector {

    private static final Map<String, GcInfo> lastGcInfoMap = new ConcurrentHashMap<>(16);

    private JvmGcV3Collector() {
        //empty
    }

    public static List<JvmGcMetricsV3> collectGcMetrics() {
        final List<GarbageCollectorMXBean> gcMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        final List<JvmGcMetricsV3> result = new ArrayList<>(gcMXBeanList.size());
        for (int i = 0, size = gcMXBeanList.size(); i < size; i++) {
            final GarbageCollectorMXBean gcMxBean = gcMXBeanList.get(i);
            final String gcName = gcMxBean.getName();
            final GcInfo curGcInfo = new GcInfo(gcMxBean);
            final GcInfo lastGcInfo = lastGcInfoMap.put(gcName, curGcInfo);
            if (lastGcInfo != null) {
                result.add(generateGcMetrics(curGcInfo, lastGcInfo));
            }
        }
        return result;
    }

    private static JvmGcMetricsV3 generateGcMetrics(GcInfo cur, GcInfo last) {
        return new JvmGcMetricsV3(cur.gcName, cur.gcCount - last.gcCount, cur.gcTime - last.gcTime);
    }

    private static final class GcInfo {

        private final String gcName;

        private final long gcTime;

        private final long gcCount;

        GcInfo(GarbageCollectorMXBean gcMxBean) {
            this.gcName = gcMxBean.getName();
            this.gcTime = gcMxBean.getCollectionTime();
            this.gcCount = gcMxBean.getCollectionCount();
        }

        @Override
        public String toString() {
            return "GcInfo{" +
                    "gcName='" + gcName + '\'' +
                    ", gcTime=" + gcTime +
                    ", gcCount=" + gcCount +
                    '}';
        }
    }
}
