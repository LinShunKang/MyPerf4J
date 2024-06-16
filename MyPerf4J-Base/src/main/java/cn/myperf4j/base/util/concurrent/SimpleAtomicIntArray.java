package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;

/**
 * Created by LinShunkang on 2020/11/24
 */
public final class SimpleAtomicIntArray implements AtomicIntArray, Serializable {

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

    public SimpleAtomicIntArray(int length) {
        array = new int[length];
    }

    @Override
    public int length() {
        return array.length;
    }

    @Override
    public int get(int index) {
        return getRaw(checkedByteOffset(index));
    }

    private int getRaw(long offset) {
        return unsafe.getIntVolatile(array, offset);
    }

    @Override
    public int getAndIncrement(int index) {
        return getAndAdd(index, 1);
    }

    @Override
    public int getAndAdd(int index, int delta) {
        return unsafe.getAndAddInt(array, checkedByteOffset(index), delta);
    }

    @Override
    public int incrementAndGet(int index) {
        return addAndGet(index, 1);
    }

    @Override
    public int addAndGet(int index, int delta) {
        return getAndAdd(index, delta) + delta;
    }

    @Override
    public void reset() {
        unsafe.setMemory(array, byteOffset(0), (long) array.length * scale, (byte) 0);
    }

    @Override
    public long fillSortedKvs(LongBuf longBuf) {
        long totalCount = 0L;
        for (int i = 0, len = array.length; i < len; ++i) {
            final int count = get(i);
            if (count > 0) {
                longBuf.write(i, count);
                totalCount += count;
            }
        }
        return totalCount;
    }
}
