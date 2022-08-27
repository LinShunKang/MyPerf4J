package cn.myperf4j.core.recorder;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.IntHashCounter;
import cn.myperf4j.base.util.concurrent.AtomicIntHashCounter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2018/3/13
 * <p>
 * 默认使用该类作为 MyPerf4J 的 Recorder
 * 该类用于精确存储某一个方法在指定时间片内的响应时间
 * 为了减小内存占用，利用 数组 + Map 的方式:
 * 1、将小于等于 mostTimeThreshold 的响应时间记录在 AtomicIntArray 中；
 * 2、将大于 mostTimeThreshold 的响应时间记录到 AtomicIntHashCounter 中。
 */
public final class AccurateRecorder extends Recorder {

    private final int mostTimeThreshold;

    private final AtomicIntArray timingArr;

    private final IntHashCounter timingHashCounter;

    private final AtomicInteger diffCount;

    private AccurateRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        super(methodTagId);
        this.mostTimeThreshold = mostTimeThreshold;
        this.timingArr = new AtomicIntArray(mostTimeThreshold);
        this.timingHashCounter = new AtomicIntHashCounter(outThresholdCount);
        this.diffCount = new AtomicInteger(0);
    }

    @Override
    public void recordTime(final long startNanoTime, final long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }

        final int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1_000_000);
        if (elapsedTime < mostTimeThreshold) {
            if (timingArr.getAndIncrement(elapsedTime) <= 0) {
                diffCount.incrementAndGet();
            }
        } else if (timingHashCounter.getAndIncrement(elapsedTime) <= 0) {
            diffCount.incrementAndGet();
        }
    }

    @Override
    public long fillSortedRecords(LongBuf longBuf) {
        return timingArr.fillSortedKvs(longBuf) + timingHashCounter.fillSortedKvs(longBuf);
    }

    @Override
    public int getDiffCount() {
        return diffCount.get();
    }

    @Override
    public void resetRecord() {
        if (hasRecord()) {
            timingArr.reset();
            timingHashCounter.reset();
            diffCount.set(0);
        }
    }

    @Override
    public boolean hasRecord() {
        return diffCount.get() > 0;
    }

    public static AccurateRecorder getInstance(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        return new AccurateRecorder(methodTagId, mostTimeThreshold, outThresholdCount);
    }
}
