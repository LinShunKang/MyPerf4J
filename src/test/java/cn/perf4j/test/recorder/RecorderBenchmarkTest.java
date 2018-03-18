package cn.perf4j.test.recorder;

import cn.perf4j.*;
import cn.perf4j.util.StopWatch;

import java.util.concurrent.*;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class RecorderBenchmarkTest {

    public static void main(String[] args) {
        AbstractRecorder recorder = Recorder.getInstance("RecorderBenchmarkTest", 100, 50);

        int times = 100000000;
        singleThreadBenchmark(recorder, times / 10);//warm up
        System.out.println(PerfStatsCalculator.calPerfStat(recorder));

        recorder.resetRecord();
        singleThreadBenchmark(recorder, times);
        System.out.println(PerfStatsCalculator.calPerfStat(recorder));

        recorder.resetRecord();
        multiThreadBenchmark(recorder, times, 2);
        System.out.println(PerfStatsCalculator.calPerfStat(recorder));

        recorder.resetRecord();
        multiThreadBenchmark(recorder, times, 4);
        System.out.println(PerfStatsCalculator.calPerfStat(recorder));

        recorder.resetRecord();
        multiThreadBenchmark(recorder, times, 8);
        System.out.println(PerfStatsCalculator.calPerfStat(recorder));
    }

    private static void singleThreadBenchmark(AbstractRecorder recorder, int times) {
        recorder.setStartMilliTime(System.currentTimeMillis());
        benchmark(recorder, times);
        recorder.setStopMilliTime(System.currentTimeMillis());
    }

    private static void benchmark(AbstractRecorder recorder, int times) {
        for (int i = 0; i < times; ++i) {
            long start = System.nanoTime();
            recorder.recordTime(start, start + ThreadLocalRandom.current().nextLong(150000000));
        }
    }

    private static void multiThreadBenchmark(final AbstractRecorder recorder, final int times, int threadCount) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1));
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        recorder.setStartMilliTime(System.currentTimeMillis());
        for (int i = 0; i < threadCount; ++i) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        benchmark(recorder, times);
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
        recorder.setStopMilliTime(System.currentTimeMillis());

        executor.shutdownNow();
    }
}
