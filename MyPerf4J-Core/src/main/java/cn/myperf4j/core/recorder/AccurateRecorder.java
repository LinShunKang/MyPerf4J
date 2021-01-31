package cn.myperf4j.core.recorder;

import cn.myperf4j.base.buffer.IntBuf;
import cn.myperf4j.base.util.collections.MapUtils;
import cn.myperf4j.base.util.concurrent.AtomicIntArray;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2018/3/13
 * <p>
 * 默认使用该类作为 MyPerf4J 的 Recorder
 * 该类用于精确存储某一个方法在指定时间片内的响应时间
 * 为了减小内存占用，利用 数组+Map 的方式:
 * 1、将小于等于 mostTimeThreshold 的响应时间记录在数组中；
 * 2、将大于 mostTimeThreshold 的响应时间记录到 Map 中。
 */
public final class AccurateRecorder extends Recorder {

    private final AtomicIntArray timingArr;

    private final ConcurrentHashMap<Integer, AtomicInteger> timingMap;

    private final AtomicInteger diffCount;

    private AccurateRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        super(methodTagId);
        this.timingArr = new AtomicIntArray(mostTimeThreshold + 1);
        this.timingMap = new ConcurrentHashMap<>(MapUtils.getFitCapacity(outThresholdCount));
        this.diffCount = new AtomicInteger(0);
    }

    @Override
    public void recordTime(final long startNanoTime, final long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }

        final int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1_000_000);
        if (elapsedTime < timingArr.length()) {
            final int oldValue = timingArr.getAndIncrement(elapsedTime);
            if (oldValue <= 0) {
                diffCount.incrementAndGet();
            }
            return;
        }

        final AtomicInteger count = timingMap.get(elapsedTime);
        if (count != null) {
            count.incrementAndGet();
            return;
        }

        final AtomicInteger oldCounter = timingMap.putIfAbsent(elapsedTime, new AtomicInteger(1));
        if (oldCounter != null) {
            oldCounter.incrementAndGet();
        } else {
            diffCount.incrementAndGet();
        }
    }

    @Override
    public long fillSortedRecords(IntBuf intBuf) {
        long totalCount = 0L;
        final AtomicIntArray timingArr = this.timingArr;
        for (int i = 0; i < timingArr.length(); ++i) {
            final int count = timingArr.get(i);
            if (count > 0) {
                intBuf.write(i, count);
                totalCount += count;
            }
        }
        return totalCount + fillMapRecord(intBuf);
    }

    private int fillMapRecord(IntBuf intBuf) {
        int totalCount = 0;
        final int offset = intBuf.writerIndex();
        final ConcurrentHashMap<Integer, AtomicInteger> timingMap = this.timingMap;
        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            final int count = entry.getValue().get();
            if (count > 0) {
                intBuf.write(entry.getKey());
                totalCount += count;
            }
        }

        if (offset == intBuf.writerIndex()) {
            return 0;
        }

        final int writerIndex = intBuf.writerIndex();
        Arrays.sort(intBuf._buf(), offset, writerIndex);

        for (int i = writerIndex - 1; i >= offset; --i) {
            final int count = intBuf.getInt(i);
            final int keyIdx = (i << 1) - offset; //2 * (i - offset) + offset
            intBuf.setInt(keyIdx, count);
            intBuf.setInt(keyIdx + 1, timingMap.get(count).get());
        }
        intBuf.writerIndex((writerIndex << 1) - offset); //writerIndex + (writerIndex - offset)
        return totalCount;
    }

    @Override
    public int getDiffCount() {
        return diffCount.get();
    }

    @Override
    public void resetRecord() {
        if (!hasRecord()) {
            return;
        }

        timingArr.reset();
        final int eliminateThreshold = 3 * timingArr.length() / 2;
        final Iterator<Entry<Integer, AtomicInteger>> iterator = timingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Integer, AtomicInteger> entry = iterator.next();
            if (entry.getKey() > eliminateThreshold || entry.getValue().get() <= 0) {
                iterator.remove();
            } else {
                entry.getValue().set(0);
            }
        }

        diffCount.set(0);
    }

    @Override
    public boolean hasRecord() {
        return diffCount.get() > 0;
    }

    public static AccurateRecorder getInstance(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        return new AccurateRecorder(methodTagId, mostTimeThreshold, outThresholdCount);
    }
}
