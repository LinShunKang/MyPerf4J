package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2021/03/19
 */
public class AtomicIntHashCounterTest {

    @Test
    public void testSimpleIncrease() {
        final IntHashCounter intCounter = new AtomicIntHashCounter(1);
        Assertions.assertEquals(1, intCounter.incrementAndGet(1));
        Assertions.assertEquals(1, intCounter.get(1));
        Assertions.assertEquals(3, intCounter.addAndGet(1, 2));
        Assertions.assertEquals(3, intCounter.get(1));
        Assertions.assertEquals(3, intCounter.getAndIncrement(1));
        Assertions.assertEquals(4, intCounter.get(1));
        Assertions.assertEquals(4, intCounter.getAndAdd(1, 2));
        Assertions.assertEquals(6, intCounter.get(1));

        Assertions.assertEquals(1, intCounter.size());
        intCounter.reset();
        Assertions.assertEquals(0, intCounter.size());
    }

    @Test
    public void testIncrease() {
        final int testTimes = 10240;
        final IntHashCounter intCounter = new AtomicIntHashCounter(testTimes);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < testTimes; j++) {
                intCounter.addAndGet(j + 1, j + 2);
            }
        }

        Assertions.assertEquals(testTimes, intCounter.size());
        for (int i = 0; i < testTimes; i++) {
            Assertions.assertEquals((i + 2) * 2, intCounter.get(i + 1));
        }
    }

    @Test
    public void testSize() {
        final IntHashCounter intCounter = new AtomicIntHashCounter();
        Assertions.assertEquals(0, intCounter.size());

        intCounter.addAndGet(1, 2);
        Assertions.assertEquals(1, intCounter.size());

        intCounter.addAndGet(2, 2);
        Assertions.assertEquals(2, intCounter.size());

        for (int i = 1; i < 5; i++) {
            intCounter.addAndGet(i, i);
        }
        Assertions.assertEquals(4, intCounter.size());
    }

    @Test
    public void testReset() {
        final IntHashCounter intCounter = new AtomicIntHashCounter();
        Assertions.assertEquals(0, intCounter.size());

        final int testTimes = 10240;
        for (int i = 0; i < testTimes; i++) {
            Assertions.assertEquals(1, intCounter.addAndGet(i, 1));
        }
        Assertions.assertEquals(testTimes, intCounter.size());

        intCounter.reset();
        for (int i = 1; i < testTimes; i++) {
            Assertions.assertEquals(0, intCounter.get(i));
        }
        Assertions.assertEquals(0, intCounter.size());

        for (int i = 0; i < testTimes; i++) {
            Assertions.assertEquals(i + 10, intCounter.addAndGet(i, i + 10));
        }
        Assertions.assertEquals(testTimes, intCounter.size());

        for (int i = 0; i < testTimes; i++) {
            Assertions.assertEquals(i + 10, intCounter.get(i));
        }
        Assertions.assertEquals(testTimes, intCounter.size());
    }

    @Test
    public void testConflict() {
        final IntHashCounter intCounter = new AtomicIntHashCounter(8);
        for (int i = 0; i < 2; i++) {
            intCounter.incrementAndGet(0);
            intCounter.incrementAndGet(8);
            intCounter.incrementAndGet(16);
        }

        for (int i = 0; i < 2; i++) {
            intCounter.get(0);
            intCounter.get(8);
            intCounter.get(16);
        }
    }

    @Test
    public void testFillSortedKvs() {
        final IntHashCounter intCounter = new AtomicIntHashCounter(8);
        for (int i = 0; i < 16; i++) {
            intCounter.incrementAndGet(i);
        }

        try (LongBuf longBuf = new LongBuf(16)) {
            Assertions.assertEquals(16, intCounter.fillSortedKvs(longBuf));
            for (int i = 0; i < longBuf.writerIndex(); i++) {
                final long kv = longBuf.getLong(i);
                Assertions.assertEquals(i, (int) kv, "i=" + i);
                Assertions.assertEquals(1, (int) (kv >> 32), "i=" + i);
            }
        }
    }

    @Test
    public void testSingleThread() {
        final IntHashCounter intMap = new AtomicIntHashCounter(128 * 1024);
        final AtomicIntegerArray intArray = new AtomicIntegerArray(128 * 1024);
        final ConcurrentMap<Integer, AtomicInteger> integerMap = new ConcurrentHashMap<>(128 * 1024);
        mode1(intMap, intArray, integerMap, 1024, 64);
        mode1(intMap, intArray, integerMap, 256, 256);
        mode1(intMap, intArray, integerMap, 64, 1024);
        mode1(intMap, intArray, integerMap, 16, 4 * 1024);
        mode1(intMap, intArray, integerMap, 4, 16 * 1024);
        mode1(intMap, intArray, integerMap, 1, 64 * 1024);

        integerMap.forEach((k, v) -> {
            Assertions.assertEquals(v.intValue(), intArray.get(k), "intArray");
            Assertions.assertEquals(v.intValue(), intMap.get(k), "intMap");
        });
    }

    private void mode1(IntHashCounter intMap,
                       AtomicIntegerArray intArray,
                       ConcurrentMap<Integer, AtomicInteger> integerMap,
                       int x,
                       int y) {
        final long start = System.nanoTime();
        for (int i = 0; i < x; i++) {
            for (int j = 1; j < y; j++) {
                intMap.incrementAndGet(j);
                intArray.incrementAndGet(j);
                increase(integerMap, j, 1);
            }
        }
        Logger.info("x=" + x + ", y=" + y + ", cost=" + (System.nanoTime() - start) / 1000_000 + "ms");
    }

    @Test
    public void testSingleThreadV2() {
        final IntHashCounter intMap = new AtomicIntHashCounter(16);
        for (int i = 1; i < 1024; i++) {
            intMap.incrementAndGet(i);
        }
        System.out.println(intMap);
    }

    @Test
    public void testMultiThread4HighRace() throws InterruptedException, BrokenBarrierException {
        int failureTimes = 0;
        final int testTimes = Integer.getInteger("MyPerf4J.aih.testTimes", 1024);
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final int threadCnt = Math.max(Runtime.getRuntime().availableProcessors() - 2, 1);
        final ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        for (int i = 0; i < testTimes; i++) {
            System.out.printf("--------------------- Round %d start ---------------------\n", i);
            final int randomKeyBound = random.nextInt(18, 514);
            final int randomDeltaBound = random.nextInt(2, 16);
            if (!testMultiThread0(executor, threadCnt, randomKeyBound, randomDeltaBound)) {
                failureTimes++;
            }
            System.out.printf("--------------------- Round %d stop, already has %d failure ---------------------\n\n",
                    i, failureTimes);
        }
        System.out.println("testMultiThread4ManyKeys(): failureTimes=" + failureTimes +
                ", failureRate=" + (1.0D * failureTimes / testTimes));
    }

    @Test
    public void testMultiThread4LowRace() throws InterruptedException, BrokenBarrierException {
        int failureTimes = 0;
        final int testTimes = Integer.getInteger("MyPerf4J.aih.testTimes", 1024);
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final int threadCnt = Math.max(Runtime.getRuntime().availableProcessors() - 2, 1);
        final ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        for (int i = 0; i < testTimes; i++) {
            System.out.printf("--------------------- Round %d start ---------------------\n", i);
            final int randomKeyBound = random.nextInt(514, 1024 * 1024);
            final int randomDeltaBound = random.nextInt(2, 16);
            if (!testMultiThread0(executor, threadCnt, randomKeyBound, randomDeltaBound)) {
                failureTimes++;
            }
            System.out.printf("--------------------- Round %d stop, already has %d failure ---------------------\n\n",
                    i, failureTimes);
        }
        System.out.println("testMultiThread4LowRace(): failureTimes=" + failureTimes +
                ", failureRate=" + (1.0D * failureTimes / testTimes));
    }

    private static boolean testMultiThread0(final ExecutorService executor,
                                            final int threadCnt,
                                            final int randomKeyBound,
                                            final int randomDeltaBound)
            throws InterruptedException, BrokenBarrierException {
        final int testTimes = ThreadLocalRandom.current().nextInt(0, 8 * 1024);
        final IntHashCounter intMap = new AtomicIntHashCounter();
        final ConcurrentMap<Integer, AtomicInteger> integerMap = new ConcurrentHashMap<>(testTimes);
        final CyclicBarrier barrier = new CyclicBarrier(threadCnt + 1);
        for (int i = 0; i < threadCnt; i++) {
            executor.execute(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace(System.err);
                }

                try {
                    final ThreadLocalRandom random = ThreadLocalRandom.current();
                    for (int k = 0; k < testTimes; k++) {
                        final int randomKey = random.nextInt(0, randomKeyBound);
                        final int randomDelta = random.nextInt(1, randomDeltaBound);
                        intMap.getAndAdd(randomKey, randomDelta);
                        increase(integerMap, randomKey, randomDelta);
                    }
                } finally {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace(System.err);
                    }
                }
            });
        }
        barrier.await();

        final long start = System.nanoTime();
        System.out.printf("M starting, threadCnt=%d, testTimes=%d, randomKeyBound=%d, randomDeltaBound=%d\n",
                threadCnt, testTimes, randomKeyBound, randomDeltaBound);

        barrier.await();
        System.out.printf("Cost %dms, size=%d\n", (System.nanoTime() - start) / 1_000_000L, integerMap.size());

        integerMap.forEach((k, v) -> Assertions.assertEquals(v.get(), intMap.get(k)));
        Assertions.assertEquals(integerMap.size(), intMap.size());
        System.out.println("Congratulations!");
        return true;
    }

    private static void increase(ConcurrentMap<Integer, AtomicInteger> integerHashMap, int k, int delta) {
        final AtomicInteger count = integerHashMap.get(k);
        if (count != null) {
            count.addAndGet(delta);
            return;
        }

        final AtomicInteger oldCounter = integerHashMap.putIfAbsent(k, new AtomicInteger(delta));
        if (oldCounter != null) {
            oldCounter.addAndGet(delta);
        }
    }
}
