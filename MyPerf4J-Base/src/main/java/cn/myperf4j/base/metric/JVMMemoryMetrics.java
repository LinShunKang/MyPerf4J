package cn.myperf4j.base.metric;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JVMMemoryMetrics extends Metrics {

    private static final long serialVersionUID = -1501873958253505089L;

    private long nonHeapInit;

    private long nonHeapUsed;

    private long nonHeapCommitted;

    private long nonHeapMax;


    private long heapInit;

    private long heapUsed;

    private long heapCommitted;

    private long heapMax;


    private long codeCacheInit;

    private long codeCacheUsed;

    private long codeCacheCommitted;

    private long codeCacheMax;


    public long getNonHeapInit() {
        return nonHeapInit;
    }

    public void setNonHeapInit(long nonHeapInit) {
        this.nonHeapInit = nonHeapInit;
    }

    public long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public void setNonHeapUsed(long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
    }

    public long getNonHeapCommitted() {
        return nonHeapCommitted;
    }

    public void setNonHeapCommitted(long nonHeapCommitted) {
        this.nonHeapCommitted = nonHeapCommitted;
    }

    public long getNonHeapMax() {
        return nonHeapMax;
    }

    public void setNonHeapMax(long nonHeapMax) {
        this.nonHeapMax = nonHeapMax;
    }

    public long getHeapInit() {
        return heapInit;
    }

    public void setHeapInit(long heapInit) {
        this.heapInit = heapInit;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public long getHeapCommitted() {
        return heapCommitted;
    }

    public void setHeapCommitted(long heapCommitted) {
        this.heapCommitted = heapCommitted;
    }

    public long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(long heapMax) {
        this.heapMax = heapMax;
    }

    public long getCodeCacheInit() {
        return codeCacheInit;
    }

    public void setCodeCacheInit(long codeCacheInit) {
        this.codeCacheInit = codeCacheInit;
    }

    public long getCodeCacheUsed() {
        return codeCacheUsed;
    }

    public void setCodeCacheUsed(long codeCacheUsed) {
        this.codeCacheUsed = codeCacheUsed;
    }

    public long getCodeCacheCommitted() {
        return codeCacheCommitted;
    }

    public void setCodeCacheCommitted(long codeCacheCommitted) {
        this.codeCacheCommitted = codeCacheCommitted;
    }

    public long getCodeCacheMax() {
        return codeCacheMax;
    }

    public void setCodeCacheMax(long codeCacheMax) {
        this.codeCacheMax = codeCacheMax;
    }

    @Override
    public String toString() {
        return "JVMMemoryMetrics{" +
                "nonHeapInit=" + nonHeapInit +
                ", nonHeapUsed=" + nonHeapUsed +
                ", nonHeapCommitted=" + nonHeapCommitted +
                ", nonHeapMax=" + nonHeapMax +
                ", heapInit=" + heapInit +
                ", heapUsed=" + heapUsed +
                ", heapCommitted=" + heapCommitted +
                ", heapMax=" + heapMax +
                ", codeCacheInit=" + codeCacheInit +
                ", codeCacheUsed=" + codeCacheUsed +
                ", codeCacheCommitted=" + codeCacheCommitted +
                ", codeCacheMax=" + codeCacheMax +
                '}';
    }

    /**
     * -XX:+UseG1GC
     * @param args
     */
    public static void main(String[] args) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        System.out.println("nonHeapMemoryUsage: " + nonHeapMemoryUsage.toString());

        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        System.out.println("heapMemoryUsage: " + heapMemoryUsage.toString());

        System.out.println("ObjectPendingFinalizationCount: " + memoryMXBean.getObjectPendingFinalizationCount());

//        memoryMXBean.


        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            System.out.println(memoryPoolMXBean.getName() + " " + memoryPoolMXBean.getCollectionUsage());
        }
    }
}
