package cn.myperf4j.base.metric;

import java.lang.management.ClassLoadingMXBean;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JvmClassMetrics extends Metrics {

    private static final long serialVersionUID = 5189910445931453667L;

    private long total;

    private long loaded;

    private long unloaded;

    public JvmClassMetrics(long total, long loaded, long unloaded) {
        this.total = total;
        this.loaded = loaded;
        this.unloaded = unloaded;
    }

    public JvmClassMetrics(ClassLoadingMXBean bean) {
        this(bean.getTotalLoadedClassCount(), bean.getLoadedClassCount(), bean.getUnloadedClassCount());
    }

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
        return "JvmClassMetrics{" +
                "total=" + total +
                ", loaded=" + loaded +
                ", unloaded=" + unloaded +
                '}';
    }

}
