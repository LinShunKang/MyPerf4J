package MyPerf4J;

import cn.myperf4j.core.AbstractRecorder;
import cn.myperf4j.core.AccurateRecorder;
import cn.myperf4j.core.util.PerfStatsCalculator;

import java.util.concurrent.*;

/**
 * Created by LinShunkang on 2018/4/19
 */
public class RecorderBenchmarkTest {

    public static void main(String[] args) {
        AbstractRecorder recorder = AccurateRecorder.getInstance("RecorderBenchmarkTest", 100, 50);

        int times = 100000000;
        singleThreadBenchmark(recorder, times / 10);//warm up
        System.out.println(PerfStatsCalculator.calPerfStats(recorder));

        recorder.resetRecord();
        singleThreadBenchmark(recorder, times);
        System.out.println(PerfStatsCalculator.calPerfStats(recorder));

        recorder.resetRecord();
        multiThreadBenchmark(recorder, times, 2);
        System.out.println(PerfStatsCalculator.calPerfStats(recorder));

        recorder.resetRecord();
        multiThreadBenchmark(recorder, times, 4);
        System.out.println(PerfStatsCalculator.calPerfStats(recorder));

        recorder.resetRecord();
        multiThreadBenchmark(recorder, times, 8);
        System.out.println(PerfStatsCalculator.calPerfStats(recorder));
    }

    private static void singleThreadBenchmark(AbstractRecorder recorder, int times) {
        recorder.setStartTime(System.currentTimeMillis());
        benchmark(recorder, times);
        recorder.setStopTime(System.currentTimeMillis());
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
        recorder.setStartTime(System.currentTimeMillis());
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
        recorder.setStopTime(System.currentTimeMillis());

        executor.shutdownNow();
    }
}