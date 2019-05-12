package cn.myperf4j.base.metric;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import static java.lang.Thread.State.*;
import static java.lang.Thread.State.TERMINATED;
import static java.lang.Thread.State.TIMED_WAITING;

/**
 * Created by LinShunkang on 2018/8/19
 */
public class JvmThreadMetrics extends Metrics {

    private static final long serialVersionUID = 8514109576224018139L;

    private final long totalStarted;

    private final int active;

    private final int peak;

    private final int daemon;

    private final int news;

    private final int runnable;

    private final int blocked;

    private final int waiting;

    private final int timedWaiting;

    private final int terminated;

    public JvmThreadMetrics(ThreadMXBean bean) {
        this.totalStarted = bean.getTotalStartedThreadCount();
        this.active = bean.getThreadCount();
        this.peak = bean.getPeakThreadCount();
        this.daemon = bean.getDaemonThreadCount();

        int threadsNew = 0;
        int threadsRunnable = 0;
        int threadsBlocked = 0;
        int threadsWaiting = 0;
        int threadsTimedWaiting = 0;
        int threadsTerminated = 0;

        ThreadInfo[] threadInfoArr = bean.getThreadInfo(bean.getAllThreadIds(), 0);
        for (int i = 0; i < threadInfoArr.length; ++i) {
            ThreadInfo threadInfo = threadInfoArr[i];
            if (threadInfo == null) {
                continue;
            }

            Thread.State state = threadInfo.getThreadState();
            if (state == NEW) {
                threadsNew++;
            } else if (state == RUNNABLE) {
                threadsRunnable++;
            } else if (state == BLOCKED) {
                threadsBlocked++;
            } else if (state == WAITING) {
                threadsWaiting++;
            } else if (state == TIMED_WAITING) {
                threadsTimedWaiting++;
            } else if (state == TERMINATED) {
                threadsTerminated++;
            }
        }

        this.news = threadsNew;
        this.runnable = threadsRunnable;
        this.blocked = threadsBlocked;
        this.waiting = threadsWaiting;
        this.timedWaiting = threadsTimedWaiting;
        this.terminated = threadsTerminated;
    }

    public long getTotalStarted() {
        return totalStarted;
    }

    public int getActive() {
        return active;
    }

    public int getPeak() {
        return peak;
    }

    public int getDaemon() {
        return daemon;
    }

    public int getNews() {
        return news;
    }

    public int getRunnable() {
        return runnable;
    }

    public int getBlocked() {
        return blocked;
    }

    public int getWaiting() {
        return waiting;
    }

    public int getTimedWaiting() {
        return timedWaiting;
    }

    public int getTerminated() {
        return terminated;
    }

    @Override
    public String toString() {
        return "JvmThreadMetrics{" +
                "totalStarted=" + totalStarted +
                ", active=" + active +
                ", peak=" + peak +
                ", daemon=" + daemon +
                ", news=" + news +
                ", runnable=" + runnable +
                ", blocked=" + blocked +
                ", waiting=" + waiting +
                ", timedWaiting=" + timedWaiting +
                ", terminated=" + terminated +
                "} " + super.toString();
    }

}
