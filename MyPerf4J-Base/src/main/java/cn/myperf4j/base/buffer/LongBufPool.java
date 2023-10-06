package cn.myperf4j.base.buffer;

import cn.myperf4j.base.util.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by LinShunkang on 2022/08/14
 */
public final class LongBufPool {

    private static final int FOUR_K = 4 * 1024;
    private static final int TEN_K = 10 * 1024;
    private static final int ONE_HUNDRED_K = 100 * 1024;
    private static final int MAX_CAPACITY = ONE_HUNDRED_K;

    private static final int FOUR_K_COUNT = 10;
    private static final int TEN_K_COUNT = 2;
    private static final int ONE_HUNDRED_K_COUNT = 1;

    //用于存放int[4 * 1024]，理论上可以容纳 4096 个不同的响应时间
    private final Queue<LongBuf> fourKQueue = new ArrayDeque<>();

    //用于存放int[10 * 1024]，理论上可以容纳 10240 个不同的响应时间
    private final Queue<LongBuf> tenKQueue = new ArrayDeque<>();

    //用于存放int[100 * 1024]，理论上可以容纳 102400 个不同的响应时间
    private final Queue<LongBuf> oneHundredKQueue = new ArrayDeque<>();

    private static final class LongBufPoolHolder {
        private static final LongBufPool instance = new LongBufPool();
    }

    public static LongBufPool getInstance() {
        return LongBufPool.LongBufPoolHolder.instance;
    }

    private LongBufPool() {
        initBufQueue(fourKQueue, FOUR_K, FOUR_K_COUNT);
        initBufQueue(tenKQueue, TEN_K, TEN_K_COUNT);
        initBufQueue(oneHundredKQueue, ONE_HUNDRED_K, ONE_HUNDRED_K_COUNT);
    }

    private void initBufQueue(Queue<LongBuf> queue, int capacity, int num) {
        for (int i = 0; i < num; ++i) {
            queue.add(new LongBuf(capacity, this));
        }
    }

    public synchronized LongBuf acquire(int capacity) {
        if (capacity > MAX_CAPACITY) {
            return new LongBuf(capacity);
        }

        LongBuf buf;
        if (capacity <= FOUR_K) {
            buf = fourKQueue.poll();
        } else if (capacity <= TEN_K) {
            buf = tenKQueue.poll();
        } else {
            buf = oneHundredKQueue.poll();
        }
        return buf != null ? buf : new LongBuf(capacity);
    }

    public synchronized void release(LongBuf buf) {
        final int capacity = buf.capacity();
        if (capacity > MAX_CAPACITY || buf.pool() != this) {
            return;
        }

        if (capacity == FOUR_K) {
            fourKQueue.offer(buf);
        } else if (capacity == TEN_K) {
            tenKQueue.offer(buf);
        } else if (capacity == ONE_HUNDRED_K) {
            oneHundredKQueue.offer(buf);
        } else {
            Logger.warn("LongBufPool.release(" + capacity + ") buf.capacity() is invalid!!!");
        }
    }
}
