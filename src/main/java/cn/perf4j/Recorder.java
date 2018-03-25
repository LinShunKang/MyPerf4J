package cn.perf4j;

import cn.perf4j.util.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2018/3/13
 */

/**
 * 该类用于存储某一个api在指定时间片内的响应时间
 * 为了减小内存占用，利用数组+Map的方式:
 * 1、将小于mostTimeThreshold的响应时间记录在数组中；
 * 2、将大于等于mostTimeThreshold的响应时间记录到Map中。
 */
public class Recorder extends AbstractRecorder {

    private final int mostTimeThreshold;

    private final AtomicIntegerArray timingArr;

    private final ConcurrentHashMap<Integer, AtomicInteger> timingMap;

    private Recorder(int mostTimeThreshold, int outThresholdCount) {
        this.mostTimeThreshold = mostTimeThreshold;
        this.timingArr = new AtomicIntegerArray(mostTimeThreshold);
        this.timingMap = new ConcurrentHashMap<>(MapUtils.getFitCapacity(outThresholdCount));
    }

    @Override
    public void recordTime(long startNanoTime, long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }

        int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1000000);
        if (elapsedTime < mostTimeThreshold) {
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
    public int[] getSortedTimingRecords() {
        int idx = 0;
        int[] result = new int[getEffectiveRecordCount() * 2];
        for (int i = 0; i < timingArr.length(); ++i) {
            int count = timingArr.get(i);
            if (count > 0) {
                result[idx++] = i;
                result[idx++] = count;
            }
        }
        fillMapRecord(result, idx);
        return result;
    }

    private int getEffectiveRecordCount() {
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
    public synchronized void resetRecord() {
        for (int i = 0; i < timingArr.length(); ++i) {
            timingArr.set(i, 0);
        }

        Iterator<Map.Entry<Integer, AtomicInteger>> iterator = timingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, AtomicInteger> entry = iterator.next();
            if (entry.getKey() > (1.5 * mostTimeThreshold)) {
                iterator.remove();
            } else {
                entry.getValue().set(0);
            }
        }

        setStartMilliTime(0L);
        setStopMilliTime(0L);
    }

    public static Recorder getInstance(String api, int mostTimeThreshold, int outThresholdCount) {
        Recorder recorder = new Recorder(mostTimeThreshold, outThresholdCount);
        recorder.setApi(api);
        return recorder;
    }
}
