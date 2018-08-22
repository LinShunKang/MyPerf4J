package cn.myperf4j.base.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/4/6
 */
public final class ChunkPool {

    private static final int ONE_K = 1024;
    private static final int TEN_K = 10 * 1024;
    private static final int ONE_HUNDRED_K = 100 * 1024;

    private static final int ONE_K_COUNT = 10;
    private static final int TEN_K_COUNT = 2;
    private static final int ONE_HUNDRED_K_COUNT = 1;

    private final Set<int[]> chunkSet = new HashSet<>((int) ((ONE_K_COUNT + TEN_K_COUNT + ONE_HUNDRED_K_COUNT) / 0.5) + 1);

    private final BlockingQueue<int[]> oneKQueue = new LinkedTransferQueue<>();//用于存放int[1024]，理论上可以容纳512个不同的响应时间

    private final BlockingQueue<int[]> tenKQueue = new LinkedTransferQueue<>();//用于存放int[10 * 1024]，理论上可以容纳5120个不同的响应时间

    private final BlockingQueue<int[]> oneHundredKQueue = new LinkedTransferQueue<>();//用于存放int[100 * 1024]，理论上可以容纳51200个不同的响应时间

    private ChunkPool() {
        fillChunk(oneKQueue, ONE_K, ONE_K_COUNT);
        fillChunk(tenKQueue, TEN_K, TEN_K_COUNT);
        fillChunk(oneHundredKQueue, ONE_HUNDRED_K, ONE_HUNDRED_K_COUNT);
    }

    private void fillChunk(BlockingQueue<int[]> queue, int arrayLength, int num) {
        for (int i = 0; i < num; ++i) {
            int[] chunk = new int[arrayLength];
            chunkSet.add(chunk);
            queue.add(chunk);
        }
    }

    public int[] getChunk(int chunkLength) {
        if (chunkLength > ONE_HUNDRED_K) {
            return new int[chunkLength];
        }

        try {
            if (chunkLength <= ONE_K) {
                return oneKQueue.poll(1, TimeUnit.MILLISECONDS);
            } else if (chunkLength <= TEN_K) {
                return tenKQueue.poll(1, TimeUnit.MILLISECONDS);
            } else {
                return oneHundredKQueue.poll(1, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            Logger.error("ChunkPool.getChunk(" + chunkLength + ")", e);
        }
        return new int[chunkLength];
    }

    public void returnChunk(int[] chunk) {
        if (chunk == null || chunk.length > ONE_HUNDRED_K) {
            return;
        }

        Arrays.fill(chunk, 0);
        try {
            if (chunk.length == ONE_K && chunkSet.contains(chunk)) {
                oneKQueue.put(chunk);
            } else if (chunk.length == TEN_K && chunkSet.contains(chunk)) {
                tenKQueue.put(chunk);
            } else if (chunk.length == ONE_HUNDRED_K && chunkSet.contains(chunk)) {
                oneHundredKQueue.put(chunk);
            } else {
                Logger.warn("ChunkPool.returnChunk(" + chunk.length + ") chunk.length is invalid or chunk is not belongs to this pool!!!");
            }
        } catch (Exception e) {
            Logger.error("ChunkPool.returnChunk(" + chunk.length + ")", e);
        }
    }

    private static class IntArrayChunkPoolHolder {
        private final static ChunkPool instance = new ChunkPool();
    }

    public static ChunkPool getInstance() {
        return IntArrayChunkPoolHolder.instance;
    }
}
