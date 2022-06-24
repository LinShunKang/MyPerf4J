package cn.myperf4j.base.util.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2018/3/16
 */
public final class ThreadUtils {

    private ThreadUtils() {
        //empty
    }

    public static ThreadFactory newThreadFactory(final String prefix) {
        return new ThreadFactory() {
            final AtomicInteger threadId = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, prefix + threadId.getAndIncrement());
            }
        };
    }

    public static void sleepQuietly(long time, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(time);
        } catch (Exception e) {
            //empty
        }
    }

    public static ThreadGroup getSystemThreadGroup() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }
        return threadGroup;
    }

    public static Thread[] findThreads(final ThreadGroup group, final boolean recurse) {
        if (group == null) {
            throw new IllegalArgumentException("The group must not be null");
        }

        int count = group.activeCount();
        Thread[] threads;
        do {
            threads = new Thread[count + (count / 2) + 1]; //slightly grow the array size
            count = group.enumerate(threads, recurse);
            //return value of enumerate() must be strictly less than the array size according to javadoc
        } while (count >= threads.length);
        return threads;
    }
}
