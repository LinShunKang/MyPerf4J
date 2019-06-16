package cn.myperf4j.core.recorder;

import cn.myperf4j.base.buffer.IntBuf;
import cn.myperf4j.base.util.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2018/3/13
 */

/**
 * MyPerf4J默认使用的是 RoughRecorder，如果需要使用 AccurateRecorder，则在配置文件里加上 RecorderMode=accurate
 * 该类用于精确存储某一个方法在指定时间片内的响应时间
 * 为了减小内存占用，利用 数组+Map 的方式:
 * 1、将小于等于 mostTimeThreshold 的响应时间记录在数组中；
 * 2、将大于 mostTimeThreshold 的响应时间记录到 Map 中。
 */
public class AccurateRecorder extends Recorder {

    private final AtomicIntegerArray timingArr;

    private final ConcurrentHashMap<Integer, AtomicInteger> timingMap;

    private final AtomicInteger effectiveCount;

    private AccurateRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        super(methodTagId);
        this.timingArr = new AtomicIntegerArray(mostTimeThreshold + 1);
        this.timingMap = new ConcurrentHashMap<>(MapUtils.getFitCapacity(outThresholdCount));
        this.effectiveCount = new AtomicInteger(0);
    }

    @Override
    public void recordTime(long startNanoTime, long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }

        int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1000000);
        if (elapsedTime < timingArr.length()) {
            int oldValue = timingArr.getAndIncrement(elapsedTime);
            if (oldValue <= 0) {
                effectiveCount.incrementAndGet();
            }
            return;
        }

        AtomicInteger count = timingMap.get(elapsedTime);
        if (count != null) {
            count.incrementAndGet();
            return;
        }

        AtomicInteger oldCounter = timingMap.putIfAbsent(elapsedTime, new AtomicInteger(1));
        if (oldCounter != null) {
            oldCounter.incrementAndGet();
        } else {
            effectiveCount.incrementAndGet();
        }
    }

    @Override
    public void fillSortedRecords(IntBuf intBuf) {
        for (int i = 0; i < timingArr.length(); ++i) {
            int count = timingArr.get(i);
            if (count > 0) {
                intBuf.write(i);
                intBuf.write(count);
            }
        }
        fillMapRecord(intBuf);
    }

    private void fillMapRecord(IntBuf intBuf) {
        int offset = intBuf.writerIndex();
        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                intBuf.write(entry.getKey());
            }
        }

        int writerIndex = intBuf.writerIndex();
        Arrays.sort(intBuf._buf(), offset, writerIndex);

        for (int i = writerIndex - 1; i >= offset; --i) {
            int count = intBuf.getInt(i);
            int keyIdx = 2 * i - offset;//2 * (i - offset) + offset
            intBuf.setInt(keyIdx, count);
            intBuf.setInt(keyIdx + 1, timingMap.get(count).get());
        }
        intBuf.writerIndex(writerIndex + (writerIndex - offset));
    }

    @Override
    public int getEffectiveCount() {
        return effectiveCount.get();
    }

    @Override
    public synchronized void resetRecord() {
        for (int i = 0; i < timingArr.length(); ++i) {
            timingArr.set(i, 0);
        }

        Iterator<Map.Entry<Integer, AtomicInteger>> iterator = timingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, AtomicInteger> entry = iterator.next();
            if ((entry.getKey() > 1.5 * timingArr.length())
                    || entry.getValue().get() <= 0) {
                iterator.remove();
            } else {
                entry.getValue().set(0);
            }
        }

        effectiveCount.set(0);
    }

    @Override
    public boolean hasRecord() {
        return effectiveCount.get() > 0;
    }

    public static AccurateRecorder getInstance(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        return new AccurateRecorder(methodTagId, mostTimeThreshold, outThresholdCount);
    }
}
