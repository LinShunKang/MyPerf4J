package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmClassMetrics;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * Created by LinShunkang on 2019/07/10
 */
public final class JvmClassCollector {

    private static final ClassLoadingMXBean CLASS_LOADING_MX_BEAN = ManagementFactory.getClassLoadingMXBean();

    private JvmClassCollector() {
        //empty
    }

    public static JvmClassMetrics collectClassMetrics() {
        ClassLoadingMXBean mxBean = CLASS_LOADING_MX_BEAN;
        return new JvmClassMetrics(mxBean.getTotalLoadedClassCount(),
                mxBean.getLoadedClassCount(),
                mxBean.getUnloadedClassCount());
    }
}
