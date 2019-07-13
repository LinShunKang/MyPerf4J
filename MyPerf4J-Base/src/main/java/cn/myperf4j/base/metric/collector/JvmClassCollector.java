package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmClassMetrics;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * Created by LinShunkang on 2019/07/10
 */
public final class JvmClassCollector {

    public static JvmClassMetrics collectClassMetrics() {
        ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
        return new JvmClassMetrics(bean.getTotalLoadedClassCount(), bean.getLoadedClassCount(), bean.getUnloadedClassCount());
    }

}
