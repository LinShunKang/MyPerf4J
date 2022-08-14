package cn.myperf4j.base.buffer;

import cn.myperf4j.base.util.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
    private final BlockingQueue<LongBuf> fourKQueue = new LinkedTransferQueue<>();

    //用于存放int[10 * 1024]，理论上可以容纳 10240 个不同的响应时间
    private final BlockingQueue<LongBuf> tenKQueue = new LinkedTransferQueue<>();

    //用于存放int[100 * 1024]，理论上可以容纳 102400 个不同的响应时间
    private final BlockingQueue<LongBuf> oneHundredKQueue = new LinkedTransferQueue<>();

    private static class LongBufPoolHolder {
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

    private void initBufQueue(BlockingQueue<LongBuf> queue, int capacity, int num) {
        for (int i = 0; i < num; ++i) {
            queue.add(new LongBuf(capacity, this));
        }
    }

    public LongBuf acquire(int capacity) {
        if (capacity > MAX_CAPACITY) {
            return new LongBuf(capacity);
        }

        try {
            if (capacity <= FOUR_K) {
                return fourKQueue.poll(1, MILLISECONDS);
            } else if (capacity <= TEN_K) {
                return tenKQueue.poll(1, MILLISECONDS);
            } else {
                return oneHundredKQueue.poll(1, MILLISECONDS);
            }
        } catch (Exception e) {
            Logger.error("LongBufPool.acquire(" + capacity + ")", e);
        }
        return new LongBuf(capacity);
    }

    public void release(LongBuf buf) {
        if (buf == null || buf.capacity() > MAX_CAPACITY || buf.pool() != this) {
            return;
        }

        buf.reset();
        try {
            if (buf.capacity() == FOUR_K) {
                fourKQueue.put(buf);
            } else if (buf.capacity() == TEN_K) {
                tenKQueue.put(buf);
            } else if (buf.capacity() == ONE_HUNDRED_K) {
                oneHundredKQueue.put(buf);
            } else {
                Logger.warn("LongBufPool.release(" + buf.capacity() + ") buf.capacity() is invalid!!!");
            }
        } catch (Exception e) {
            Logger.error("LongBufPool.release(" + buf.capacity() + ")", e);
        }
    }
}
