package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmThreadMetrics;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.NEW;
import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.TERMINATED;
import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;

/**
 * Created by LinShunkang on 2019/07/08
 */
public final class JvmThreadCollector {

    private static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();

    private JvmThreadCollector() {
        //empty
    }

    public static JvmThreadMetrics collectThreadMetrics() {
        int news = 0;
        int runnable = 0;
        int blocked = 0;
        int waiting = 0;
        int timedWaiting = 0;
        int terminated = 0;
        final ThreadMXBean mxBean = THREAD_MX_BEAN;
        final ThreadInfo[] threadInfoArr = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 0);
        for (int i = 0; i < threadInfoArr.length; ++i) {
            final ThreadInfo threadInfo = threadInfoArr[i];
            if (threadInfo == null) {
                continue;
            }

            final State state = threadInfo.getThreadState();
            if (state == NEW) {
                news++;
            } else if (state == RUNNABLE) {
                runnable++;
            } else if (state == BLOCKED) {
                blocked++;
            } else if (state == WAITING) {
                waiting++;
            } else if (state == TIMED_WAITING) {
                timedWaiting++;
            } else if (state == TERMINATED) {
                terminated++;
            }
        }

        return new JvmThreadMetrics(
                mxBean.getTotalStartedThreadCount(),
                mxBean.getThreadCount(),
                mxBean.getPeakThreadCount(),
                mxBean.getDaemonThreadCount(),
                news,
                runnable,
                blocked,
                waiting,
                timedWaiting,
                terminated);
    }
}
