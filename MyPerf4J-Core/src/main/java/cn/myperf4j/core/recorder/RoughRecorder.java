package cn.myperf4j.core.recorder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2018/3/25
 */

/**
 * 默认使用该类作为 MyPerf4J 的 Recorder
 * <p>
 * 该类用于粗略存储某一个方法在指定时间片内的响应时间
 * 为了进一步减小内存占用，只利用数组方式:
 * 1、将小于等于 mostTimeThreshold 的响应时间记录在数组中；
 * 2、将大于 mostTimeThreshold 的响应时间记录到 timingArr[mostTimeThreshold+1]中。
 * <p>
 * 注意：由于该 Recorder 会将大于 mostTimeThreshold 的响应时间记录为 mostTimeThreshold+1
 * 所以为了保证 RoughRecorder 记录的准确性，请把 mostTimeThreshold 设置的偏大一些。
 */
public class RoughRecorder extends Recorder {

    private final AtomicIntegerArray timingArr;

    private final AtomicInteger outThresholdCounter;


    public RoughRecorder(int methodTag, int mostTimeThreshold) {
        super(methodTag);
        this.timingArr = new AtomicIntegerArray(mostTimeThreshold + 2);
        this.outThresholdCounter = new AtomicInteger(0);
    }

    @Override
    public void recordTime(long startNanoTime, long endNanoTime) {
        if (startNanoTime > endNanoTime) {
            return;
        }
        hasRecord = true;

        int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1000000);
        if (elapsedTime < timingArr.length() - 1) {
            timingArr.incrementAndGet(elapsedTime);
        } else {
            timingArr.incrementAndGet(timingArr.length() - 1);
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
        return result;
    }

    @Override
    public synchronized void resetRecord() {
        for (int i = 0; i < timingArr.length(); ++i) {
            timingArr.set(i, 0);
        }

        hasRecord = false;
    }

    @Override
    public int getOutThresholdCount() {
        return outThresholdCounter.get();
    }

    public static RoughRecorder getInstance(int methodTagId, int mostTimeThreshold) {
        return new RoughRecorder(methodTagId, mostTimeThreshold);
    }
}
