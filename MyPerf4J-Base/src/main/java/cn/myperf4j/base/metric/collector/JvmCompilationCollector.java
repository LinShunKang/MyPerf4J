package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmCompilationMetrics;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

/**
 * Created by LinShunkang on 2019/11/03
 */
public final class JvmCompilationCollector {

    private static volatile long lastTime;

    private static final CompilationMXBean COMPILATION_MX_BEAN = ManagementFactory.getCompilationMXBean();

    private JvmCompilationCollector() {
        //empty
    }

    public static JvmCompilationMetrics collectCompilationMetrics() {
        CompilationMXBean mxBean = COMPILATION_MX_BEAN;
        if (mxBean != null && mxBean.isCompilationTimeMonitoringSupported()) {
            long totalTime = mxBean.getTotalCompilationTime();
            long timeInterval = totalTime - lastTime;
            lastTime = totalTime;
            return new JvmCompilationMetrics(timeInterval, totalTime);
        }
        return new JvmCompilationMetrics(0L, 0L);
    }
}
