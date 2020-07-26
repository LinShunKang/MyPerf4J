package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorders;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/4/19
 */
public final class RecorderBenchmarkTest {

    private RecorderBenchmarkTest() {
        //empty
    }

    public static void main(String[] args) {
        AtomicReferenceArray<Recorder> recorderArr = new AtomicReferenceArray<>(1);
        Recorder recorder = AccurateRecorder.getInstance(0, 100, 50);
//        Recorder recorder = RoughRecorder.getInstance(0, 100);
        recorderArr.set(0, recorder);

        Recorders recorders = new Recorders(recorderArr);
        MethodTag methodTag = MethodTag.getGeneralInstance("", "", "", "", "");

        int times = 100000000;
        singleThreadBenchmark(recorders, times / 10); //warm up
        System.out.println(MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(),
                recorders.getStopTime()));

        recorder.resetRecord();
        singleThreadBenchmark(recorders, times);
        System.out.println(MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(),
                recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 2);
        System.out.println(MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(),
                recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 4);
        System.out.println(MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(),
                recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 8);
        System.out.println(MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(),
                recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 16);
        System.out.println(MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(),
                recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 32);
        System.out.println(MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(),
                recorders.getStopTime()));
    }

    private static void singleThreadBenchmark(Recorders recorders, int times) {
        recorders.setStartTime(System.currentTimeMillis());
        benchmark(recorders, times);
        recorders.setStopTime(System.currentTimeMillis());
    }

    private static void benchmark(Recorders recorders, int times) {
        Recorder recorder = recorders.getRecorder(0);
        for (int i = 0; i < times; ++i) {
            long start = System.nanoTime();
            recorder.recordTime(start, start + ThreadLocalRandom.current().nextLong(150000000));
        }
    }

    private static void multiThreadBenchmark(final Recorders recorders, final int times, int threadCount) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount, 1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(1));
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        recorders.setStartTime(System.currentTimeMillis());
        for (int i = 0; i < threadCount; ++i) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        benchmark(recorders, times);
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recorders.setStopTime(System.currentTimeMillis());

        executor.shutdownNow();
    }
}
