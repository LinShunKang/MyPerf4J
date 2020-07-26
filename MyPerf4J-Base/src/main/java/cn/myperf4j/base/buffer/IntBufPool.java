package cn.myperf4j.base.buffer;

import cn.myperf4j.base.util.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2019/06/13
 */
public final class IntBufPool {

    private static final int ONE_K = 1024;
    private static final int TEN_K = 10 * 1024;
    private static final int ONE_HUNDRED_K = 100 * 1024;
    private static final int MAX_CAPACITY = ONE_HUNDRED_K;

    private static final int ONE_K_COUNT = 10;
    private static final int TEN_K_COUNT = 2;
    private static final int ONE_HUNDRED_K_COUNT = 1;

    //用于存放int[1024]，理论上可以容纳512个不同的响应时间
    private final BlockingQueue<IntBuf> oneKQueue = new LinkedTransferQueue<>();

    //用于存放int[10 * 1024]，理论上可以容纳5120个不同的响应时间
    private final BlockingQueue<IntBuf> tenKQueue = new LinkedTransferQueue<>();

    //用于存放int[100 * 1024]，理论上可以容纳51200个不同的响应时间
    private final BlockingQueue<IntBuf> oneHundredKQueue = new LinkedTransferQueue<>();

    private static class IntBufPoolHolder {
        private static final IntBufPool instance = new IntBufPool();
    }

    public static IntBufPool getInstance() {
        return IntBufPool.IntBufPoolHolder.instance;
    }

    private IntBufPool() {
        initBufQueue(oneKQueue, ONE_K, ONE_K_COUNT);
        initBufQueue(tenKQueue, TEN_K, TEN_K_COUNT);
        initBufQueue(oneHundredKQueue, ONE_HUNDRED_K, ONE_HUNDRED_K_COUNT);
    }

    private void initBufQueue(BlockingQueue<IntBuf> queue, int capacity, int num) {
        for (int i = 0; i < num; ++i) {
            queue.add(new IntBuf(capacity, this));
        }
    }

    public IntBuf acquire(int capacity) {
        if (capacity > MAX_CAPACITY) {
            return new IntBuf(capacity);
        }

        try {
            if (capacity <= ONE_K) {
                return oneKQueue.poll(1, TimeUnit.MILLISECONDS);
            } else if (capacity <= TEN_K) {
                return tenKQueue.poll(1, TimeUnit.MILLISECONDS);
            } else {
                return oneHundredKQueue.poll(1, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            Logger.error("IntBufPool.acquire(" + capacity + ")", e);
        }
        return new IntBuf(capacity);
    }

    public void release(IntBuf buf) {
        if (buf == null || buf.capacity() > MAX_CAPACITY || buf.intBufPool() != this) {
            return;
        }

        buf.reset();
        try {
            if (buf.capacity() == ONE_K) {
                oneKQueue.put(buf);
            } else if (buf.capacity() == TEN_K) {
                tenKQueue.put(buf);
            } else if (buf.capacity() == ONE_HUNDRED_K) {
                oneHundredKQueue.put(buf);
            } else {
                Logger.warn("IntBufPool.release(" + buf.capacity() + ") buf.capacity() is invalid!!!");
            }
        } catch (Exception e) {
            Logger.error("IntBufPool.release(" + buf.capacity() + ")", e);
        }
    }
}
