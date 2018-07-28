package cn.myperf4j.core;

import cn.myperf4j.core.util.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2018/3/13
 */

/**
 * MyPerf4J默认使用的是RoughRecorder，如果需要使用AccurateRecorder，则在启动参数里加上-DMyPerf4J.recorder.mode=accurate
 * 该类用于精确存储某一个api在指定时间片内的响应时间
 * 为了减小内存占用，利用数组+Map的方式:
 * 1、将小于等于mostTimeThreshold的响应时间记录在数组中；
 * 2、将大于mostTimeThreshold的响应时间记录到Map中。
 */
public class AccurateRecorder extends Recorder {

    private final AtomicIntegerArray timingArr;

    private final ConcurrentHashMap<Integer, AtomicInteger> timingMap;

    private AccurateRecorder(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        super(methodTagId);
        this.timingArr = new AtomicIntegerArray(mostTimeThreshold + 1);
        this.timingMap = new ConcurrentHashMap<>(MapUtils.getFitCapacity(outThresholdCount));
    }

    @Override
    public void recordTime(long startNanoTime, long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }
        hasRecord = true;

        int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1000000);
        if (elapsedTime < timingArr.length()) {
            timingArr.incrementAndGet(elapsedTime);
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
        }
    }

    @Override
    public void fillSortedRecords(int[] arr) {
        int idx = 0;
        for (int i = 0; i < timingArr.length(); ++i) {
            int count = timingArr.get(i);
            if (count > 0) {
                arr[idx++] = i;
                arr[idx++] = count;
            }
        }
        fillMapRecord(arr, idx);
    }

    private void fillMapRecord(int[] arr, int offset) {
        int idx = offset;
        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                arr[idx++] = entry.getKey();
            }
        }

        Arrays.sort(arr, offset, idx);
        for (int i = idx - 1; i >= offset; --i) {
            arr[2 * i - offset] = arr[i];
            arr[2 * i + 1 - offset] = timingMap.get(arr[i]).get();
        }
    }

    @Override
    public int getEffectiveCount() {
        int result = 0;
        for (int i = 0; i < timingArr.length(); ++i) {
            int count = timingArr.get(i);
            if (count > 0) {
                result++;
            }
        }

        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                result++;
            }
        }
        return result;
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

        hasRecord = false;
    }

    @Override
    public int getOutThresholdCount() {
        return timingMap.size();//粗略估计
    }

    public static AccurateRecorder getInstance(int methodTagId, int mostTimeThreshold, int outThresholdCount) {
        return new AccurateRecorder(methodTagId, mostTimeThreshold, outThresholdCount);
    }
}
