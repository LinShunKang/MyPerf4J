package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;

import static java.lang.Thread.currentThread;

/**
 * Created by LinShunkang on 2024/02/14
 */
public final class FastAtomicIntArrayV1 implements Serializable {

    private static final long serialVersionUID = -4361761107900070905L;

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = Unsafe.ARRAY_INT_BASE_OFFSET;
    private static final int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
    private static final int shift = 31 - Integer.numberOfLeadingZeros(scale);
    private static final int falseSharingShift = 31 - Integer.numberOfLeadingZeros(64 / scale);

    static {
        if (!isPowerOfTwo(scale)) {
            throw new Error("data type scale not a power of two");
        }
    }

    private static boolean isPowerOfTwo(int i) {
        return (i & (i - 1)) == 0;
    }

    private static long byteOffset(int i) {
        return ((long) i << shift) + base;
    }

    private final int shards;

    private final int actualLength;

    private final int actualShift;

    // int[n][m]: [A11,A21,A31,....,An1, A12,A22,A32,....,An2, A1m,....,Anm]
    private final int[] array;

    public FastAtomicIntArrayV1(int length) {
        this(length, 4);
    }

    public FastAtomicIntArrayV1(int length, int shards) {
        if (!isPowerOfTwo(shards)) {
            throw new IllegalArgumentException("shards not a power of two");
        }

        this.shards = shards;
        this.actualShift = falseSharingShift + (31 - Integer.numberOfLeadingZeros(shards));
        this.actualLength = length << actualShift;
        this.array = new int[actualLength];
    }

    private long checkedByteOffset(int i) {
        if (i < 0 || i >= this.actualLength) {
            throw new IndexOutOfBoundsException("index " + i);
        }
        return byteOffset(i);
    }

    public int length() {
        return this.actualLength >> actualShift;
    }

    public int get(int i) {
        int result = 0;
        final int baseIdx = i << actualShift;
        for (int shardIdx = 0; shardIdx < shards; shardIdx++) {
            result += getRaw(array, checkedByteOffset(baseIdx + (shardIdx << falseSharingShift)));
        }
        return result;
    }

    private int getRaw(int[] array, long offset) {
        return unsafe.getIntVolatile(array, offset);
    }

    public int getAndIncrement(int i) {
        return addAndGet(i, 1);
    }

    public int addAndGet(int i, int delta) {
        final int[] array = this.array;
        final int baseIdx = i << actualShift;
        final int shardIdx = (shards - 1) & hash(currentThread().getId());
        final long byteOffset = checkedByteOffset(baseIdx + (shardIdx << falseSharingShift));
        while (true) {
            final int current = getRaw(array, byteOffset);
            if (unsafe.compareAndSwapInt(array, byteOffset, current, current + delta)) {
                return current;
            }
        }
    }

    private int hash(long threadId) {
        return (int) (threadId >>> 16 ^ threadId);
    }

    public void reset() {
        unsafe.setMemory(array, byteOffset(0), (long) array.length * scale, (byte) 0);
    }

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
