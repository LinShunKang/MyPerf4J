package cn.myperf4j.base.metric;

import java.lang.management.MemoryUsage;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JvmMemoryMetrics extends Metrics {

    private static final long serialVersionUID = -1501873958253505089L;

    private final long nonHeapInit;

    private final long nonHeapUsed;

    private final long nonHeapCommitted;

    private final long nonHeapMax;


    private final long heapInit;

    private final long heapUsed;

    private final long heapCommitted;

    private final long heapMax;

    public JvmMemoryMetrics(MemoryUsage nonHeapMem, MemoryUsage heapMem) {
        this.nonHeapInit = nonHeapMem.getInit();
        this.nonHeapUsed = nonHeapMem.getUsed();
        this.nonHeapCommitted = nonHeapMem.getCommitted();
        this.nonHeapMax = nonHeapMem.getMax();
        this.heapInit = heapMem.getInit();
        this.heapUsed = heapMem.getUsed();
        this.heapCommitted = heapMem.getCommitted();
        this.heapMax = heapMem.getMax();
    }

    public long getNonHeapInit() {
        return nonHeapInit;
    }

    public long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public long getNonHeapCommitted() {
        return nonHeapCommitted;
    }

    public long getNonHeapMax() {
        return nonHeapMax;
    }

    public long getHeapInit() {
        return heapInit;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public long getHeapCommitted() {
        return heapCommitted;
    }

    public long getHeapMax() {
        return heapMax;
    }

    @Override
    public String toString() {
        return "JvmMemoryMetrics{" +
                "nonHeapInit=" + nonHeapInit +
                ", nonHeapUsed=" + nonHeapUsed +
                ", nonHeapCommitted=" + nonHeapCommitted +
                ", nonHeapMax=" + nonHeapMax +
                ", heapInit=" + heapInit +
                ", heapUsed=" + heapUsed +
                ", heapCommitted=" + heapCommitted +
                ", heapMax=" + heapMax +
                '}';
    }
}
