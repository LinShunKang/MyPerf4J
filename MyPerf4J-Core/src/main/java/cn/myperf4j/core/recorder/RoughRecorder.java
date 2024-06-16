package cn.myperf4j.core.recorder;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.SimpleAtomicIntArray;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2018/3/25
 * <p>
 * MyPerf4J默认使用的是 AccurateRecorder，如果需要使用 RoughRecorder，则在配置文件里加上 RecorderMode=rough
 * <p>
 * 该类用于粗略存储某一个方法在指定时间片内的响应时间
 * 为了进一步减小内存占用，只利用数组方式:
 * 1、将小于等于 mostTimeThreshold 的响应时间记录在数组中；
 * 2、将大于 mostTimeThreshold 的响应时间记录到 timingArr[mostTimeThreshold+1]中。
 * <p>
 * 注意：由于该 Recorder 会将大于 mostTimeThreshold 的响应时间记录为 mostTimeThreshold+1
 * 所以为了保证 RoughRecorder 记录的准确性，请把 mostTimeThreshold 设置的偏大一些。
 */
public final class RoughRecorder extends Recorder {

    private final AtomicIntArray timingArr;

    private final AtomicInteger diffCount;

    public RoughRecorder(int methodTag, int mostTimeThreshold) {
        super(methodTag);
        this.timingArr = new SimpleAtomicIntArray(mostTimeThreshold + 2);
        this.diffCount = new AtomicInteger(0);
    }

    @Override
    public void recordTime(final long startNanoTime, final long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }

        int oldValue;
        final int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1000000);
        final AtomicIntArray timingArr = this.timingArr;
        if (elapsedTime < timingArr.length() - 1) {
            oldValue = timingArr.getAndIncrement(elapsedTime);
        } else {
            oldValue = timingArr.getAndIncrement(timingArr.length() - 1);
        }

        if (oldValue <= 0) {
            diffCount.incrementAndGet();
        }
    }

    @Override
    public long fillSortedRecords(LongBuf longBuf) {
        return timingArr.fillSortedKvs(longBuf);
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
        diffCount.set(0);
    }

    @Override
    public boolean hasRecord() {
        return diffCount.get() > 0;
    }

    public static RoughRecorder getInstance(int methodTagId, int mostTimeThreshold) {
        return new RoughRecorder(methodTagId, mostTimeThreshold);
    }
}
