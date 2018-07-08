package cn.myperf4j.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2018/3/25
 */

/**
 * 默认使用该类作为MyPerf4J的Recorder
 * <p>
 * 该类用于粗略存储某一个api在指定时间片内的响应时间
 * 为了进一步减小内存占用，只利用数组方式:
 * 1、将小于mostTimeThreshold的响应时间记录在数组中；
 * 2、将大于等于mostTimeThreshold*2的响应时间丢弃。
 * <p>
 * 注意：由于该Recorder只记录小于mostTimeThreshold的响应时间，
 * 所以为了保证RoughRecorder记录的准确性，请把mostTimeThreshold设置的偏大一些。
 */
public class RoughRecorder extends Recorder {

    private final AtomicIntegerArray timingArr;

    private final AtomicInteger outThresholdCounter;


    public RoughRecorder(int mostTimeThreshold) {
        this.timingArr = new AtomicIntegerArray(mostTimeThreshold);
        this.outThresholdCounter = new AtomicInteger(0);
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

        //丢弃timingArr无法存储的记录！！！
        outThresholdCounter.incrementAndGet();
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

    public static RoughRecorder getInstance(String api, int mostTimeThreshold) {
        RoughRecorder recorder = new RoughRecorder(mostTimeThreshold);
        recorder.setTag(api);
        return recorder;
    }
}
