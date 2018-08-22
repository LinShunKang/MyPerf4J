package cn.myperf4j.base.metric;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JVMClassMetrics extends Metrics {

    private static final long serialVersionUID = 5189910445931453667L;

    private long total;

    private long loaded;

    private long unloaded;


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getLoaded() {
        return loaded;
    }

    public void setLoaded(long loaded) {
        this.loaded = loaded;
    }

    public long getUnloaded() {
        return unloaded;
    }

    public void setUnloaded(long unloaded) {
        this.unloaded = unloaded;
    }

    @Override
    public String toString() {
        return "JVMClassMetrics{" +
                "total=" + total +
                ", loaded=" + loaded +
                ", unloaded=" + unloaded +
                '}';
    }

    public static void main(String[] args) {
        ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
        System.out.println(bean.getLoadedClassCount());
        System.out.println(bean.getTotalLoadedClassCount());
        System.out.println(bean.getUnloadedClassCount());
    }
}
