package cn.myperf4j.base.util.concurrent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by LinShunkang on 2024/02/14
 */
public class FastAtomicIntArrayTest {

    private final FastAtomicIntArray atomicIntArray = new FastAtomicIntArray(1024);

    @Before
    public void clear() {
        atomicIntArray.reset();
    }

    @Test
    public void testReset() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            atomicIntArray.increment(i);
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
            atomicIntArray.increment(i);
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(1, atomicIntArray.get(i));
        }

        for (int i = 0; i < length; i++) {
            atomicIntArray.increment(i);
        }

        for (int i = 0; i < length; i++) {
            Assert.assertEquals(2, atomicIntArray.get(i));
        }
    }
}
