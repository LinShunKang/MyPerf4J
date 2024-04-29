package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;

import static cn.myperf4j.base.util.NumUtils.parseKey;
import static cn.myperf4j.base.util.NumUtils.parseValue;

/**
 * Created by LinShunkang on 2024/02/14
 */
public class FastAtomicIntArrayV2Test {

    private final FastAtomicIntArrayV2 atomicIntArray = new FastAtomicIntArrayV2(1024);

    @Before
    public void clear() {
        atomicIntArray.reset();
    }

    @Test
    public void testReset() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            atomicIntArray.incrementAndGet(i);
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(1, atomicIntArray.get(i));
        }

        atomicIntArray.reset();

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(0, atomicIntArray.get(i));
        }
    }

    @Test
    public void testIncrement() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            atomicIntArray.incrementAndGet(i);
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(1, atomicIntArray.get(i));
        }

        for (int i = 0; i < length; i++) {
            atomicIntArray.incrementAndGet(i);
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(2, atomicIntArray.get(i));
        }
    }

    @Test
    public void testFillSortedKvs() {
        final int length = atomicIntArray.length();
        for (int i = 1; i < length; i++) {
            atomicIntArray.addAndGet(i, i);
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(i, atomicIntArray.get(i));
        }

        final LongBuf longBuf = new LongBuf(length);
        Assert.assertEquals(((long) length * (length - 1)) / 2, atomicIntArray.fillSortedKvs(longBuf));

        for (int i = 0; i < longBuf.writerIndex() - 1; i++) {
            final long kv = longBuf.getLong(i);
            final int key = parseKey(kv);
            final int value = parseValue(kv);
            System.out.printf("i=%d key=%d value=%d%n", i, key, value);
            Assert.assertEquals(i + 1, key);
            Assert.assertEquals(i + 1, value);
        }
    }

    @Test
    public void testSingleThread() {
        final AtomicIntegerArray intArray = new AtomicIntegerArray(128 * 1024);
        final FastAtomicIntArrayV2 fastIntArray = new FastAtomicIntArrayV2(128 * 1024);
        mode1(intArray, fastIntArray, 1024, 64);
        mode1(intArray, fastIntArray, 256, 256);
        mode1(intArray, fastIntArray, 64, 1024);
        mode1(intArray, fastIntArray, 16, 4 * 1024);
        mode1(intArray, fastIntArray, 4, 16 * 1024);
        mode1(intArray, fastIntArray, 1, 64 * 1024);

        for (int i = 0; i < intArray.length(); i++) {
            Assert.assertEquals(intArray.get(i), fastIntArray.get(i));
        }
    }

    private void mode1(AtomicIntegerArray intArray,
                       FastAtomicIntArrayV2 fastIntArray,
                       int x,
                       int y) {
        final long start = System.nanoTime();
        for (int i = 0; i < x; i++) {
            for (int j = 1; j < y; j++) {
                intArray.incrementAndGet(j);
                fastIntArray.incrementAndGet(j);
            }
        }
        Logger.info("x=" + x + ", y=" + y + ", cost=" + (System.nanoTime() - start) / 1000_000 + "ms");
    }

    @Test
    public void testMultiThread4HighRace() throws InterruptedException, BrokenBarrierException {
        final int threadCnt = Math.max(Runtime.getRuntime().availableProcessors() - 2, 1);
        final ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        int failureTimes = 0;
//        final int testTimes = 1024 * 1024;
        final int testTimes = 16 * 1024;
        final ThreadLocalRandom random = ThreadLocalRandom.current();
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
        final int threadCnt = Math.max(Runtime.getRuntime().availableProcessors() - 2, 1);
        final ExecutorService executor = Executors.newFixedThreadPool(threadCnt);
        int failureTimes = 0;
//        final int testTimes = 1024 * 1024;
        final int testTimes = 16 * 1024;
        final ThreadLocalRandom random = ThreadLocalRandom.current();
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
        final AtomicIntegerArray intArray = new AtomicIntegerArray(randomKeyBound);
        final FastAtomicIntArrayV2 fastIntArray = new FastAtomicIntArrayV2(randomKeyBound);
        final CyclicBarrier barrier = new CyclicBarrier(threadCnt + 1);
        for (int i = 0; i < threadCnt; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                    try {
                        final ThreadLocalRandom random = ThreadLocalRandom.current();
                        for (int k = 0; k < testTimes; k++) {
                            final int randomKey = random.nextInt(0, randomKeyBound);
                            final int randomDelta = random.nextInt(1, randomDeltaBound);
                            intArray.addAndGet(randomKey, randomDelta);
                            fastIntArray.addAndGet(randomKey, randomDelta);
                        }
                    } finally {
                        try {
                            barrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        barrier.await();

        final long start = System.nanoTime();
        System.out.printf("M starting, threadCnt=%d, testTimes=%d, randomKeyBound=%d, randomDeltaBound=%d\n",
                threadCnt, testTimes, randomKeyBound, randomDeltaBound);

        barrier.await();
        System.out.printf("Cost %dms, length=%d\n", (System.nanoTime() - start) / 1_000_000L, intArray.length());

        for (int i = 0; i < intArray.length(); i++) {
            Assert.assertEquals(intArray.get(i), fastIntArray.get(i));
        }

        final LongBuf longBuf = new LongBuf(fastIntArray.length());
        fastIntArray.fillSortedKvs(longBuf);
        for (int i = 0; i < longBuf.writerIndex(); i++) {
            final long kv = longBuf.getLong(i);
            final int key = parseKey(kv);
            final int value = parseValue(kv);
            Assert.assertEquals("k=" + key, intArray.get(key), fastIntArray.get(key));
            Assert.assertEquals("k=" + key + ", v=" + value + ", kv=" + kv, intArray.get(key), value);
        }

        System.out.println("Congratulations!");
        return true;
    }
}
