package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmCompilationMetrics;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

/**
 * Created by LinShunkang on 2019/11/03
 */
public final class JvmCompilationCollector {

    private static volatile long lastCompilationTime = 0L;

    public static JvmCompilationMetrics collectCompilationMetrics() {
        CompilationMXBean mxBean = ManagementFactory.getCompilationMXBean();
        if (mxBean != null && mxBean.isCompilationTimeMonitoringSupported()) {
            long totalCompilationTime = mxBean.getTotalCompilationTime();
            JvmCompilationMetrics metrics = new JvmCompilationMetrics(totalCompilationTime - lastCompilationTime, totalCompilationTime);
            lastCompilationTime = totalCompilationTime;
            return metrics;
        }
        return new JvmCompilationMetrics(0L, 0L);
    }

}
