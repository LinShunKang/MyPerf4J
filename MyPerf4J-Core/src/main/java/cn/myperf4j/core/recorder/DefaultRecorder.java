package cn.myperf4j.core.recorder;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.AtomicIntHashCounter;
import cn.myperf4j.base.util.concurrent.IntHashCounter;
import cn.myperf4j.base.util.concurrent.SimpleAtomicIntArray;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2018/3/13
 */
public final class DefaultRecorder extends Recorder {

    private final int mostTimeThreshold;

    private final AtomicIntArray timingArr;

    private final IntHashCounter timingHashCounter;

    private final AtomicInteger diffCount;

    private DefaultRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        super(methodTagId);
        this.mostTimeThreshold = mostTimeThreshold;
        this.timingArr = new SimpleAtomicIntArray(mostTimeThreshold);
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

    public static DefaultRecorder getInstance(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        return new DefaultRecorder(methodTagId, mostTimeThreshold, outThresholdCount);
    }
}
