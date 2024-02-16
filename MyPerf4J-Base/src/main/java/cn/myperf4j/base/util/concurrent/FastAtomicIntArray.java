package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;

/**
 * Created by LinShunkang on 2024/02/14
 */
public final class FastAtomicIntArray implements Serializable {

    private static final long serialVersionUID = -4361761107900070905L;

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = Unsafe.ARRAY_INT_BASE_OFFSET;
    private static final int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
    private static final int shift = 31 - Integer.numberOfLeadingZeros(scale);

    static {
        if ((scale & (scale - 1)) != 0) {
            throw new Error("data type scale not a power of two");
        }
    }

    private static long byteOffset(int i) {
        return ((long) i << shift) + base;
    }

    private final int actualLength;

    //TODO:LSK 还可以把二维数组转变为一维数组，通过下标来逻辑分割不同数组！
    // 假设需要转换的数组为 int[n][m]，那么转换的方式有 2 种：
    // 第一种：[A11,A12,A13,....,A1m, A21,A22,A23,....,A2m, An1,....,Anm]
    // 第二种：[A11,A21,A31,....,An1, A12,A22,A32,....,An2, A1m,....,Anm]
    // 理论上第二种可以更好的利用 CPU 缓存！
    private final int[][] arrays;

    public FastAtomicIntArray(int length) {
        this.actualLength = length << 4;
        this.arrays = generateArrays(length);
    }

    private int[][] generateArrays(int length) {
//        final int[][] arrays = new int[Runtime.getRuntime().availableProcessors()][];
        final int[][] arrays = new int[16][]; //TODO:LSK 先给固定值
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = new int[length << 4];
        }
        return arrays;
    }

    private long checkedByteOffset(int i) {
        if (i < 0 || i >= this.actualLength) {
            throw new IndexOutOfBoundsException("index " + i);
        }
        return byteOffset(i);
    }

    public int length() {
        return this.actualLength >> 4;
    }

    public int get(int i) {
        int result = 0;
        final long byteOffset = checkedByteOffset(i << 4);
        for (int[] array : arrays) {
            result += getRaw(array, byteOffset);
        }
        return result;
    }

    private int getRaw(int[] array, long offset) {
        return unsafe.getIntVolatile(array, offset);
    }

    public boolean increment(int i) {
        final int[][] arrays = this.arrays;
        final int n = arrays.length;
        final int[] array = arrays[(n - 1) & hash(Thread.currentThread().getId())];
        final long byteOffset = checkedByteOffset(i << 4);
        while (true) {
            if (cas(array, byteOffset, 1)) {
                return true;
            }
        }
    }

    private int hash(long threadId) {
        return (int) (threadId >>> 16 ^ threadId);
    }

    private boolean cas(int[] array, long byteOffset, int delta) {
        final int current = getRaw(array, byteOffset);
        return unsafe.compareAndSwapInt(array, byteOffset, current, current + delta);
    }

    public void reset() {
        for (int[] array : arrays) {
            unsafe.setMemory(array, byteOffset(0), (long) array.length * scale, (byte) 0);
        }
    }

    public long fillSortedKvs(LongBuf longBuf) {
        long totalCount = 0L;
        for (int i = 0, len = this.actualLength << 4; i < len; ++i) {
            final int count = get(i);
            if (count > 0) {
                longBuf.write(i, count);
                totalCount += count;
            }
        }
        return totalCount;
    }
}
