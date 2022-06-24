package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.concurrent.ThreadUtils;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    private static final Method getThreadMethod = reflectGetThreadsMethod();

    private static final ThreadGroup systemThreadGroup = ThreadUtils.getSystemThreadGroup();

    private static Method reflectGetThreadsMethod() {
        try {
            final Method method = Thread.class.getDeclaredMethod("getThreads");
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            Logger.warn("JvmThreadCollector.reflectGetThreadsMethod(): reflect getThreads Method failure!");
            return null;
        }
    }

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
        final Thread[] threads = getThreads();
        for (int i = 0, len = threads.length; i < len; ++i) {
            final Thread thread = threads[i];
            if (thread == null) {
                continue;
            }

            final State state = thread.getState();
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

        final ThreadMXBean mxBean = THREAD_MX_BEAN;
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

    private static Thread[] getThreads() {
        if (getThreadMethod != null) {
            try {
                return (Thread[]) getThreadMethod.invoke(Thread.class);
            } catch (IllegalAccessException | InvocationTargetException e) {
                //ignore
            }
        }
        return ThreadUtils.findThreads(systemThreadGroup, true);
    }
}
