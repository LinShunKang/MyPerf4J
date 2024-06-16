package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;

import static cn.myperf4j.base.util.NumUtils.isPowerOfTwo;
import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Thread.currentThread;

/**
 * Created by LinShunkang on 2024/02/14
 */
public final class FastAtomicIntArray implements AtomicIntArray, Serializable {

    private static final long serialVersionUID = -4361761107900070905L;

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = Unsafe.ARRAY_INT_BASE_OFFSET;
    private static final int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
    private static final int shift = 31 - numberOfLeadingZeros(scale);
    private static final int falseSharingShift = 31 - numberOfLeadingZeros(64 / scale);

    static {
        if (!isPowerOfTwo(scale)) {
            throw new Error("data type scale not a power of two");
        }
    }

    private final int shards;

    private final int length;

    private final int actualLength;

    // int[n][m]: [A11,A12,A13,....,A1m, A21,A22,A23,....,A2m, An1,....,Anm]
    private final int[] array;

    public FastAtomicIntArray(int length) {
        this(length, 4);
    }

    public FastAtomicIntArray(int length, int shards) {
        if (!isPowerOfTwo(shards)) {
            throw new IllegalArgumentException("shards not a power of two");
        }

        this.shards = shards;
        this.length = length;
        this.actualLength = length << (falseSharingShift + (31 - numberOfLeadingZeros(shards)));
        this.array = new int[actualLength];
    }

    private long checkedByteOffset(int index) {
        if (index < 0 || index >= this.actualLength) {
            throw new IndexOutOfBoundsException("index " + index);
        }
        return byteOffset(index);
    }

    private static long byteOffset(int index) {
        return ((long) index << shift) + base;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public int get(int index) {
        int result = 0;
        final int[] array = this.array;
        final int length = this.length;
        for (int shardIdx = 0; shardIdx < shards; shardIdx++) {
            result += unsafe.getIntVolatile(array, checkedByteOffset((index + shardIdx * length) << falseSharingShift));
        }
        return result;
    }

    @Override
    public int getAndIncrement(int index) {
        return getAndAdd(index, 1);
    }

    @Override
    public int getAndAdd(int index, int delta) {
        final int shardIdx = (shards - 1) & hash(currentThread().getId());
        final long byteOffset = checkedByteOffset((index + shardIdx * length) << falseSharingShift);
        return unsafe.getAndAddInt(array, byteOffset, delta);
    }

    private int hash(long threadId) {
        return (int) (threadId >>> 16 ^ threadId);
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
        for (int i = 0, len = length(); i < len; ++i) {
            final int count = get(i);
            if (count > 0) {
                longBuf.write(i, count);
                totalCount += count;
            }
        }
        return totalCount;
    }
}
