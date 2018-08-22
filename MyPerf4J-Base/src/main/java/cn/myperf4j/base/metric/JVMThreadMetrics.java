package cn.myperf4j.base.metric;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JVMThreadMetrics extends Metrics {

    private static final long serialVersionUID = 8514109576224018139L;

    private int active;

    private int peak;

    private int daemon;

    private long totalStarted;


    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getPeak() {
        return peak;
    }

    public void setPeak(int peak) {
        this.peak = peak;
    }

    public int getDaemon() {
        return daemon;
    }

    public void setDaemon(int daemon) {
        this.daemon = daemon;
    }

    public long getTotalStarted() {
        return totalStarted;
    }

    public void setTotalStarted(long totalStarted) {
        this.totalStarted = totalStarted;
    }

    @Override
    public String toString() {
        return "JVMThreadMetrics{" +
                "active=" + active +
                ", peak=" + peak +
                ", daemon=" + daemon +
                ", totalStarted=" + totalStarted +
                '}';
    }

    public static void main(String[] args) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        System.out.println(threadMXBean.getPeakThreadCount());
        System.out.println(threadMXBean.getDaemonThreadCount());
        System.out.println(threadMXBean.getTotalStartedThreadCount());
        System.out.println(threadMXBean.getThreadCount());
    }

}
