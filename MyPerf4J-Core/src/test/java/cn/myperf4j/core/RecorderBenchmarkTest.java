package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.core.recorder.DefaultRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static cn.myperf4j.core.MethodMetricsCalculator.calMetrics;

/**
 * Created by LinShunkang on 2018/4/19
 */
public final class RecorderBenchmarkTest {

    private RecorderBenchmarkTest() {
        //empty
    }

    public static void main(String[] args) {
        final AtomicReferenceArray<Recorder> recorderArr = new AtomicReferenceArray<>(1);
        final Recorder recorder = DefaultRecorder.getInstance(0, 100, 50);
        recorderArr.set(0, recorder);

        final Recorders recorders = new Recorders(recorderArr);
        final MethodTag methodTag = MethodTag.getGeneralInstance("", "", "", "", "");

        final int times = 100_000_000;
        singleThreadBenchmark(recorders, times / 10); //warm up
        System.out.println(calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()));

        recorder.resetRecord();
        singleThreadBenchmark(recorders, times);
        System.out.println(calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 2);
        System.out.println(calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 4);
        System.out.println(calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 8);
        System.out.println(calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 16);
        System.out.println(calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()));

        recorder.resetRecord();
        multiThreadBenchmark(recorders, times, 32);
        System.out.println(calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()));
    }

    private static void singleThreadBenchmark(Recorders recorders, int times) {
        recorders.setStartTime(System.currentTimeMillis());
        benchmark(recorders, times);
        recorders.setStopTime(System.currentTimeMillis());
    }

    private static void benchmark(Recorders recorders, int times) {
        final Recorder recorder = recorders.getRecorder(0);
        for (int i = 0; i < times; ++i) {
            long start = System.nanoTime();
            recorder.recordTime(start, start + ThreadLocalRandom.current().nextLong(150000000));
        }
    }

    private static void multiThreadBenchmark(final Recorders recorders, final int times, int threads) {
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        final CountDownLatch countDownLatch = new CountDownLatch(threads);
        recorders.setStartTime(System.currentTimeMillis());
        for (int i = 0; i < threads; ++i) {
            executor.execute(() -> {
                try {
                    benchmark(recorders, times);
                } finally {
                    countDownLatch.countDown();
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
