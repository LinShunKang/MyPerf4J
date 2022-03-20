package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.util.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map.Entry;
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
public class FixedAtomicIntHashCounterTest {

    @Test
    public void testSimpleIncrease() {
        final FixedAtomicIntHashCounter intCounter = new FixedAtomicIntHashCounter(1);
        Assert.assertEquals(1, intCounter.incrementAndGet(1));
        Assert.assertEquals(1, intCounter.get(1));
        Assert.assertEquals(3, intCounter.addAndGet(1, 2));
        Assert.assertEquals(3, intCounter.get(1));
        Assert.assertEquals(3, intCounter.getAndIncrement(1));
        Assert.assertEquals(4, intCounter.get(1));
        Assert.assertEquals(4, intCounter.getAndAdd(1, 2));
        Assert.assertEquals(6, intCounter.get(1));

        Assert.assertEquals(1, intCounter.size());
        intCounter.reset();
        Assert.assertEquals(0, intCounter.size());
    }

    @Test
    public void testIncreaseFailure() {
        final FixedAtomicIntHashCounter intMap = new FixedAtomicIntHashCounter(8);
        for (int i = 0; i < 8; i++) {
            Assert.assertEquals(1, intMap.incrementAndGet(i));
        }
        Assert.assertEquals(8, intMap.size());

        for (int i = 8; i < 1024; i++) {
            Assert.assertEquals(0, intMap.incrementAndGet(i));
        }
        Assert.assertEquals(8, intMap.size());
    }

    @Test
    public void testIncrease() {
        final int testTimes = 10240;
        final FixedAtomicIntHashCounter intCounter = new FixedAtomicIntHashCounter(testTimes);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < testTimes; j++) {
                intCounter.addAndGet(j, j + 1);
            }
        }
        Assert.assertEquals(testTimes, intCounter.size());

        for (int i = 0; i < testTimes; i++) {
            Assert.assertEquals((i + 1) * 2, intCounter.get(i));
        }
    }

    @Test
    public void testDecrease() {
        final FixedAtomicIntHashCounter intCounter = new FixedAtomicIntHashCounter(1);
        Assert.assertEquals(-1, intCounter.addAndGet(0, -1));
        Assert.assertEquals(-1, intCounter.get(0));

        Assert.assertEquals(-3, intCounter.addAndGet(0, -2));
        Assert.assertEquals(-3, intCounter.get(0));

        Assert.assertEquals(1, intCounter.addAndGet(0, 4));
        Assert.assertEquals(1, intCounter.get(0));
    }

    @Test
    public void testSize() {
        final FixedAtomicIntHashCounter intCounter = new FixedAtomicIntHashCounter(1);
        Assert.assertEquals(intCounter.size(), 0);

        intCounter.addAndGet(1, 2);
        Assert.assertEquals(intCounter.size(), 1);

        intCounter.addAndGet(2, 2);
        Assert.assertEquals(intCounter.size(), 2);

        for (int i = 1; i < 5; i++) {
            intCounter.addAndGet(i, i);
        }
        Assert.assertEquals(4, intCounter.size());
    }

    @Test
    public void testReset() {
        final FixedAtomicIntHashCounter intCounter = new FixedAtomicIntHashCounter(10240);
        Assert.assertEquals(intCounter.size(), 0);

        for (int i = 1; i < 10240; i++) {
            Assert.assertEquals(i, intCounter.addAndGet(i, i));
        }
        Assert.assertEquals(10239, intCounter.size());

        intCounter.reset();
        Assert.assertEquals(intCounter.size(), 0);

        for (int i = 1; i < 10240; i++) {
            Assert.assertEquals(0, intCounter.get(i));
        }
    }

    @Test
    public void testConflict() {
        final FixedAtomicIntHashCounter intCounter = new FixedAtomicIntHashCounter(8);
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
    public void testSingleThread() {
        final FixedAtomicIntHashCounter intMap = new FixedAtomicIntHashCounter(128 * 1024);
        final AtomicIntegerArray intArray = new AtomicIntegerArray(128 * 1024);
        final ConcurrentMap<Integer, AtomicInteger> integerMap = new ConcurrentHashMap<>(128 * 1024);
        mode1(intMap, intArray, integerMap, 1024, 64);
        mode1(intMap, intArray, integerMap, 256, 256);
        mode1(intMap, intArray, integerMap, 64, 1024);
        mode1(intMap, intArray, integerMap, 16, 4 * 1024);
        mode1(intMap, intArray, integerMap, 4, 16 * 1024);
        mode1(intMap, intArray, integerMap, 1, 64 * 1024);

        for (Entry<Integer, AtomicInteger> entry : integerMap.entrySet()) {
            final Integer key = entry.getKey();
            final AtomicInteger value = entry.getValue();
            Assert.assertEquals("intArray", value.intValue(), intArray.get(key));
            Assert.assertEquals("intMap", value.intValue(), intMap.get(key));
        }
    }

    private void mode1(FixedAtomicIntHashCounter intMap,
                       AtomicIntegerArray intArray,
                       ConcurrentMap<Integer, AtomicInteger> integerMap,
                       int x,
                       int y) {
        final long start = System.nanoTime();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                intMap.incrementAndGet(j);
                intArray.incrementAndGet(j);
                increase(integerMap, j, 1);
            }
        }
        Logger.info("x=" + x + ", y=" + y + ", cost=" + (System.nanoTime() - start) / 1000_000 + "ms");
    }

    @Test
    public void testMultiThread() throws InterruptedException, BrokenBarrierException {
        final int threadCnt = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        for (int i = 0; i < 128; i++) {
            Logger.info("--------------------- Round " + i + " start ---------------------");
            testMultiThread0(executor, threadCnt);
            Logger.info("--------------------- Round " + i + " stop ---------------------\n");
        }
    }

    private static void testMultiThread0(ExecutorService executor, int threadCnt)
            throws InterruptedException, BrokenBarrierException {
        final int testTimes = ThreadLocalRandom.current().nextInt(1024);
        final FixedAtomicIntHashCounter intMap = new FixedAtomicIntHashCounter(testTimes);
        final AtomicIntArray intArray = new AtomicIntArray(testTimes);
        final ConcurrentMap<Integer, AtomicInteger> integerMap = new ConcurrentHashMap<>(testTimes);
        final CyclicBarrier barrier = new CyclicBarrier(threadCnt + 1);
        for (int i = 0; i < threadCnt; i++) {
            final int delta = i + 1;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    Logger.info("starting...");

                    try {
                        for (int k = 0; k < testTimes; k++) {
                            intMap.addAndGet(k, delta);
                            intArray.addAndGet(k, delta);
                            increase(integerMap, k, delta);
                        }
                    } finally {
                        try {
//                            Logger.info("stopping...");
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            e.printStackTrace();
                        } finally {
                            Logger.info("stopped.");
                        }
                    }
                }
            });
        }
        barrier.await();

        final long start = System.nanoTime();
        Logger.info("M starting, testTimes=" + testTimes + ", threadCnt=" + threadCnt);

        barrier.await();
        Logger.info("Cost " + (System.nanoTime() - start) / 1_000_000 + "ms");

        Assert.assertEquals(integerMap.toString(), testTimes, integerMap.size());
        Assert.assertEquals(intMap.toString(), testTimes, intMap.size());

        for (Entry<Integer, AtomicInteger> entry : integerMap.entrySet()) {
            final Integer key = entry.getKey();
            final int expectedVal = entry.getValue().intValue();
            Assert.assertEquals("intArray key=" + key + ", " + intArray, expectedVal, intArray.get(key));
            Assert.assertEquals("intMap key=" + key + ", " + intMap, expectedVal, intMap.get(key));
        }
        Logger.info("Congratulation!");
//        MILLISECONDS.sleep(100);
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
