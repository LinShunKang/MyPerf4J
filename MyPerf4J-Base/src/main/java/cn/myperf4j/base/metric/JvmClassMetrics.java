package cn.myperf4j.base.metric;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JvmClassMetrics extends Metrics {

    private static final long serialVersionUID = 5189910445931453667L;

    private final long total;

    private final long loaded;

    private final long unloaded;

    public JvmClassMetrics(long total, long loaded, long unloaded) {
        this.total = total;
        this.loaded = loaded;
        this.unloaded = unloaded;
    }

    public long getTotal() {
        return total;
    }

    public long getLoaded() {
        return loaded;
    }

    public long getUnloaded() {
        return unloaded;
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
