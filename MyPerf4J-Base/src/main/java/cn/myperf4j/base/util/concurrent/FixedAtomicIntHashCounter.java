package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.IntBuf;
import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.atomic.AtomicIntegerFieldUpdater.newUpdater;

/**
 * Created by LinShunkang on 2022/03/19
 */
public final class FixedAtomicIntHashCounter implements AtomicIntHashCounter, Serializable {

    private static final long serialVersionUID = 6187812026945356420L;

    private static final Unsafe unsafe = UnsafeUtils.getUnsafe();
    private static final int base = Unsafe.ARRAY_INT_BASE_OFFSET;
    private static final int scale = Unsafe.ARRAY_INT_INDEX_SCALE;
    private static final int shift = 31 - Integer.numberOfLeadingZeros(scale);

    private static final int MAX_CAPACITY = MAX_VALUE >> 1;

    private static final int MAX_TRY_TIMES = 8;

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

    private static int probeNext(int idx, int mask, int tryTimes) {
        return (idx + (2 << tryTimes)) & mask;
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
        for (int i = 0; i < MAX_TRY_TIMES; i++) {
            kOffset = byteOffset(idx, mask);
            if ((kv = getLongRaw(array, kOffset)) == 0L) {
                return 0;
            } else if (getKey(kv) == key) {
                return getValue(kv);
            }

            if ((idx = probeNext(idx, mask, i)) == startIdx) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public int getAndIncrement(int key) {
        return getAndAdd(key, 1);
    }

    @Override
    public int getAndAdd(int key, int delta) {
        if (delta == 0) {
            return get(key);
        }
        return getAndAdd0(this.array, key, delta);
    }

    private int getAndAdd0(final int[] array, final int key, final int delta) {
        final int mask = array.length - 1;
        final int startIdx = hashIdx(key, mask);
        int idx = startIdx;

        long kv, kOffset;
        for (int i = 0; i < MAX_TRY_TIMES; i++) {
            kOffset = byteOffset(idx, mask);
            if ((kv = getLongRaw(array, kOffset)) == 0L) { //try set
                if (unsafe.compareAndSwapLong(array, kOffset, 0L, getKvLong(key, delta))) {
                    SIZE_UPDATER.incrementAndGet(this);
                    return 0;
                } else if ((int) (kv = getLongRaw(array, kOffset)) == key) {
                    if (tryAddLong(array, kOffset, key, delta)) {
                        return getValue(kv);
                    } else {
                        throw new IllegalStateException("this should not have happened1!");
                    }
                }
            } else if (getKey(kv) == key) { //increase
                if (tryAddLong(array, kOffset, key, delta)) {
                    return getValue(kv);
                } else {
                    throw new IllegalStateException("this should not have happened2!");
                }
            }

            if ((idx = probeNext(idx, mask, i)) == startIdx) {
                //no more space
                return -1;
            }
        }
        return -2;
    }

    private boolean tryAddLong(final int[] array, final long byteOffset, final int key, final int delta) {
        while (true) {
            final long current = getLongRaw(array, byteOffset);
            final int value = getValue(current);
            if (key != getKey(current)/* || value <= 0*/) {
                return false;
            }

            final long next = getKvLong(key, value + delta);
            if (unsafe.compareAndSwapLong(array, byteOffset, current, next)) {
                return true;
            }
        }
    }

    @Override
    public int incrementAndGet(final int key) {
        return getAndAdd(key, 1) + 1;
    }

    @Override
    public int addAndGet(final int key, final int delta) {
        return getAndAdd(key, delta) + delta;
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
    public long fillSortedKvs(IntBuf intBuf) {
        long totalCount = 0L;
        final int offset = intBuf.writerIndex();
        final int[] array = this.array;
        for (int k = 0, len = array.length; k < len; k += 2) {
            final long kvLong = getLongRaw(array, byteOffset(k, len - 1));
            final int key = getKey(kvLong);
            final int value = getValue(kvLong);
            if (value > 0) {
                intBuf.write(key);
                totalCount += value;
            }
        }

        if (offset == intBuf.writerIndex()) {
            return 0;
        }

        final int writerIndex = intBuf.writerIndex();
        Arrays.sort(intBuf._buf(), offset, writerIndex);

        for (int i = writerIndex - 1; i >= offset; --i) {
            final int key = intBuf.getInt(i);
            final int keyIdx = (i << 1) - offset; //2 * (i - offset) + offset
            intBuf.setInt(keyIdx, key);
            intBuf.setInt(keyIdx + 1, get(key));
        }
        intBuf.writerIndex((writerIndex << 1) - offset); //writerIndex + (writerIndex - offset)
        return totalCount;
    }

    @Override
    public String toString() {
        return "FixedAtomicIntCounter{" +
                "size=" + size +
                ", array=" + Arrays.toString(array) +
                '}';
    }
}
