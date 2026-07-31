package cn.myperf4j.base.util.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by LinShunkang on 2020/11/24
 */
public abstract class AtomicIntArrayTest {

    protected AtomicIntArray atomicIntArray;

    @BeforeEach
    public void setUp() {
        this.atomicIntArray = createInstance();
    }

    protected abstract AtomicIntArray createInstance();

    @Test
    public void testReset() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            atomicIntArray.getAndAdd(i, i);
        }

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(i, atomicIntArray.get(i));
        }

        atomicIntArray.reset();

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(0, atomicIntArray.get(i));
        }
    }

    @Test
    public void testIncrementAndGet() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(1, atomicIntArray.incrementAndGet(i));
        }

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(1, atomicIntArray.get(i));
        }

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(2, atomicIntArray.incrementAndGet(i));
        }

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(2, atomicIntArray.get(i));
        }
    }

    @Test
    public void testGetAndIncrement() {
        final int length = atomicIntArray.length();
        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(0, atomicIntArray.getAndIncrement(i));
        }

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(1, atomicIntArray.get(i));
        }

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(1, atomicIntArray.getAndIncrement(i));
        }

        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(2, atomicIntArray.get(i));
        }
    }

    @Test
    public void testBitShift() {
        for (int i = 0; i < 1024; i++) {
            Assertions.assertEquals(i << 1, i * 2);
            Assertions.assertEquals(i << 1, i * (1 << 1));

            Assertions.assertEquals(i << 2, i * 4);
            Assertions.assertEquals(i << 2, i * (1 << 2));

            Assertions.assertEquals(i << 3, i * 8);
            Assertions.assertEquals(i << 3, i * (1 << 3));

            Assertions.assertEquals(i << 4, i * 16);
            Assertions.assertEquals(i << 4, i * (1 << 4));
        }
    }
}
