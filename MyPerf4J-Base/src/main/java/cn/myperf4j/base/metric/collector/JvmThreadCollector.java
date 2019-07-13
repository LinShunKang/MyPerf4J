package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmThreadMetrics;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import static java.lang.Thread.State.*;
import static java.lang.Thread.State.TERMINATED;

/**
 * Created by LinShunkang on 2019/07/08
 */
public final class JvmThreadCollector {

    public static JvmThreadMetrics collectThreadMetrics() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        int news = 0;
        int runnable = 0;
        int blocked = 0;
        int waiting = 0;
        int timedWaiting = 0;
        int terminated = 0;
        ThreadInfo[] threadInfoArr = bean.getThreadInfo(bean.getAllThreadIds(), 0);
        for (int i = 0; i < threadInfoArr.length; ++i) {
            ThreadInfo threadInfo = threadInfoArr[i];
            if (threadInfo == null) {
                continue;
            }

            Thread.State state = threadInfo.getThreadState();
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
                bean.getTotalStartedThreadCount(),
                bean.getThreadCount(),
                bean.getPeakThreadCount(),
                bean.getDaemonThreadCount(),
                news,
                runnable,
                blocked,
                waiting,
                timedWaiting,
                terminated);
    }
}
