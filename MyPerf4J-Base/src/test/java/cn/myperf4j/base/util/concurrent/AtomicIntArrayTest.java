package cn.myperf4j.base.util.concurrent;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2020/11/24
 */
public class AtomicIntArrayTest {

    protected AtomicIntArray atomicIntArray;

    @Test
    public void testReset() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            atomicIntArray.getAndAdd(i, i);
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(i, atomicIntArray.get(i));
        }

        atomicIntArray.reset();

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(0, atomicIntArray.get(i));
        }
    }

    @Test
    public void testIncrementAndGet() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            Assert.assertEquals(1, atomicIntArray.incrementAndGet(i));
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(1, atomicIntArray.get(i));
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(2, atomicIntArray.incrementAndGet(i));
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(2, atomicIntArray.get(i));
        }
    }

    @Test
    public void testGetAndIncrement() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            Assert.assertEquals(0, atomicIntArray.getAndIncrement(i));
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(1, atomicIntArray.get(i));
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(1, atomicIntArray.getAndIncrement(i));
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(2, atomicIntArray.get(i));
        }
    }

    @Test
    public void testBitShift() {
        for (int i = 0; i < 1024; i++) {
            Assert.assertEquals(i << 1, i * 2);
            Assert.assertEquals(i << 1, i * (1 << 1));

            Assert.assertEquals(i << 2, i * 4);
            Assert.assertEquals(i << 2, i * (1 << 2));

            Assert.assertEquals(i << 3, i * 8);
            Assert.assertEquals(i << 3, i * (1 << 3));

            Assert.assertEquals(i << 4, i * 16);
            Assert.assertEquals(i << 4, i * (1 << 4));
        }
    }
}
