package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater;

/**
 * Created by LinShunkang on 2022/03/19
 * <p>
 * 个人能力有限，至今还未写出可动态扩容的实现，故先实现一个不可扩容的版本，就当做是抛砖引玉，欢迎各位提交 PR！
 * <p>
 * Personal ability is limited, so far no implementation that can be dynamically expanded has been written, so to
 * implement a non-expandable version first, it will be regarded as an introduction, and you are welcome to submit PR!
 */
public final class FixedAtomicIntHashCounter implements AtomicIntHashCounter, Serializable {

    private static final long serialVersionUID = 6187812026945356420L;

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = Unsafe.ARRAY_INT_BASE_OFFSET;
    private static final int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
    private static final int shift = 31 - Integer.numberOfLeadingZeros(scale);

    private static final int MAX_CAPACITY = MAX_VALUE >> 1;

    private static final AtomicIntegerFieldUpdater<FixedAtomicIntHashCounter> SIZE_UPDATER;

    static {
        try {
            SIZE_UPDATER = newUpdater(FixedAtomicIntHashCounter.class, "size");
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private final int[] array;

    private volatile int size;

    static {
        if ((scale & (scale - 1)) != 0) {
            throw new Error("data type scale not a power of two");
        }
    }

    private static long byteOffset(int idx, int maxIdx) {
        if (idx < 0 || idx > maxIdx) {
            throw new IndexOutOfBoundsException("index " + idx);
        }
        return ((long) idx << shift) + base;
    }

    private static int tableSizeFor(int capacity) {
        int number = 1;
        while (number < capacity) {
            number = number << 1;
        }
        return number <= 8 ? 16 : number;
    }

    private static int hashIdx(int key, int mask) {
        return ((key & mask) << 1) & mask;
    }

    private static int probeNext(int idx, int mask) {
        return (idx + 2) & mask;
    }

    private static long getLongRaw(int[] array, long offset) {
        return unsafe.getLongVolatile(array, offset);
    }

    private static long getKvLong(int key, int value) {
        return ((long) value) << 32 | key;
    }

    private static int getKey(long kvLong) {
        return (int) kvLong;
    }

    private static int getValue(long kvLong) {
        return (int) (kvLong >> 32);
    }

    public FixedAtomicIntHashCounter(int initialCapacity) {
        if (initialCapacity >= MAX_CAPACITY) {
            throw new IllegalArgumentException("Max initialCapacity need low than " + MAX_CAPACITY);
        }

        this.array = new int[tableSizeFor(initialCapacity << 1)];
        this.size = 0;
    }

    @Override
    public int get(final int key) {
        final int[] array = this.array;
        final int mask = array.length - 1;
        final int startIdx = hashIdx(key, mask);
        int idx = startIdx;

        long kv, kOffset;
        while (true) {
            kOffset = byteOffset(idx, mask);
            if ((kv = getLongRaw(array, kOffset)) == 0L) {
                return 0;
            } else if (getKey(kv) == key) {
                return getValue(kv);
            }

            if ((idx = probeNext(idx, mask)) == startIdx) {
                return 0;
            }
        }
    }

    @Override
    public int incrementAndGet(final int key) {
        return addAndGet(key, 1);
    }

    @Override
    public int addAndGet(final int key, final int delta) {
        if (delta == 0) {
            return get(key);
        }
        return addAndGet0(this.array, key, delta);
    }

    private int addAndGet0(final int[] array, final int key, final int delta) {
        final int mask = array.length - 1;
        final int startIdx = hashIdx(key, mask);
        int idx = startIdx;

        long kv, kOffset;
        while (true) {
            kOffset = byteOffset(idx, mask);
            if ((kv = getLongRaw(array, kOffset)) == 0L) { //try set
                if (unsafe.compareAndSwapLong(array, kOffset, 0L, getKvLong(key, delta))) {
                    SIZE_UPDATER.incrementAndGet(this);
                    return delta;
                } else if ((int) (kv = getLongRaw(array, kOffset)) == key) {
                    if (tryAddLong(array, kOffset, key, delta)) {
                        return getValue(kv) + delta;
                    } else {
                        throw new IllegalStateException("this should not have happened1!");
                    }
                }
            } else if (getKey(kv) == key) { //increase
                if (tryAddLong(array, kOffset, key, delta)) {
                    return getValue(kv) + delta;
                } else {
                    throw new IllegalStateException("this should not have happened2!");
                }
            }

            if ((idx = probeNext(idx, mask)) == startIdx) {
                //no more space
                return -1;
            }
        }
    }

    private boolean tryAddLong(final int[] array, final long byteOffset, final int key, final int delta) {
        while (true) {
            final long current = getLongRaw(array, byteOffset);
            final int value = getValue(current);
            if (key != getKey(current) || value == 0) {
                return false;
            }

            final long next = getKvLong(key, value + delta);
            if (unsafe.compareAndSwapLong(array, byteOffset, current, next)) {
                return true;
            }
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void reset() {
        final int[] array = this.array;
        unsafe.setMemory(array, byteOffset(0, array.length - 1), (long) array.length * scale, (byte) 0);
        this.size = 0;
    }

    @Override
    public String toString() {
        return "FixedAtomicIntCounter{" +
                "array=" + Arrays.toString(array) +
                ", size=" + size +
                '}';
    }
}
