package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;

/**
 * Created by LinShunkang on 2020/11/24
 */
public final class MyAtomicIntArray implements Serializable {

    private static final long serialVersionUID = 4512166855752664301L;

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = Unsafe.ARRAY_INT_BASE_OFFSET;
    private static final int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
    private static final int shift = 31 - Integer.numberOfLeadingZeros(scale);
    private final int[] array;

    static {
        if ((scale & (scale - 1)) != 0) {
            throw new Error("data type scale not a power of two");
        }
    }

    private long checkedByteOffset(int i) {
        if (i < 0 || i >= array.length) {
            throw new IndexOutOfBoundsException("index " + i);
        }
        return byteOffset(i);
    }

    private static long byteOffset(int i) {
        return ((long) i << shift) + base;
    }

    /**
     * Creates a new AtomicIntegerArray of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public MyAtomicIntArray(int length) {
        array = new int[length];
    }

    /**
     * Returns the length of the array.
     *
     * @return the length of the array
     */
    public int length() {
        return array.length;
    }

    /**
     * Gets the current value at position {@code i}.
     *
     * @param i the index
     * @return the current value
     */
    public int get(int i) {
        return getRaw(checkedByteOffset(i));
    }

    private int getRaw(long offset) {
        return unsafe.getIntVolatile(array, offset);
    }

    /**
     * Sets the element at position {@code i} to the given value.
     *
     * @param i        the index
     * @param newValue the new value
     */
    public void set(int i, int newValue) {
        unsafe.putIntVolatile(array, checkedByteOffset(i), newValue);
    }

    /**
     * Atomically increments by one the element at index {@code i}.
     *
     * @param i the index
     * @return the previous value
     */
    public int getAndIncrement(int i) {
        return getAndAdd(i, 1);
    }

    /**
     * Atomically adds the given value to the element at index {@code i}.
     *
     * @param i     the index
     * @param delta the value to add
     * @return the previous value
     */
    public int getAndAdd(int i, int delta) {
        final long offset = checkedByteOffset(i);
        while (true) {
            int current = getRaw(offset);
            if (compareAndSetRaw(offset, current, current + delta)) {
                return current;
            }
        }
    }

    private boolean compareAndSetRaw(long offset, int expect, int update) {
        return unsafe.compareAndSwapInt(array, offset, expect, update);
    }

    /**
     * Atomically increments by one the element at index {@code i}.
     *
     * @param i the index
     * @return the updated value
     */
    public int incrementAndGet(int i) {
        return addAndGet(i, 1);
    }

    /**
     * Atomically adds the given value to the element at index {@code i}.
     *
     * @param i     the index
     * @param delta the value to add
     * @return the updated value
     */
    public int addAndGet(int i, int delta) {
        final long offset = checkedByteOffset(i);
        while (true) {
            int current = getRaw(offset);
            int next = current + delta;
            if (compareAndSetRaw(offset, current, next)) {
                return next;
            }
        }
    }

    public void reset() {
        final int[] array = this.array;
        unsafe.setMemory(array, byteOffset(0), (long) array.length * scale, ((byte) (0)));
    }
}
