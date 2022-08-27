package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import static cn.myperf4j.base.util.UnsafeUtils.fieldOffset;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.concurrent.atomic.AtomicLongFieldUpdater.newUpdater;
import static java.util.concurrent.atomic.AtomicReferenceFieldUpdater.newUpdater;

/**
 * Created by LinShunkang on 2022/07/17
 * <p>
 * This class draws on the NonBlockingHashMapLong written by Cliff Click in the JCTools project.
 */
public class AtomicIntHashCounter implements IntHashCounter {

    private static final Unsafe UNSAFE = UnsafeUtils.getUnsafe();

    private static final int I_BASE = Unsafe.ARRAY_INT_BASE_OFFSET;

    private static final int I_SCALE = Unsafe.ARRAY_INT_INDEX_SCALE;

    private static final int I_SHIFT = 31 - Integer.numberOfLeadingZeros(I_SCALE);

    private static final long IHC_OFFSET = fieldOffset(AtomicIntHashCounter.class, "ihc");

    private static final long VAL_0_OFFSET = fieldOffset(AtomicIntHashCounter.class, "val0");

    private static final int MAX_CAPACITY = MAX_VALUE >> 1;

    private static final int MIN_LOG_SIZE = 4;

    private static final int MAX_LOG_SIZE = 29;

    private static final int MIN_SIZE = 1 << MIN_LOG_SIZE; // Must be power of 2

    private static final int RE_PROBE_LIMIT = 10;

    private static final int NO_KEY = 0;

    private static final int PRIME_MASK = 1 << 31;

    private static final int TOMB_PRIME = MIN_VALUE;

    static {
        if ((I_SCALE & (I_SCALE - 1)) != 0) {
            throw new Error("data type scale not a power of two");
        }
    }

    private IHC ihc;

    private int val0; // Value for Key: NO_KEY

    public AtomicIntHashCounter() {
        this(MIN_SIZE);
    }

    public AtomicIntHashCounter(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity: " + initialCapacity + " (expected: >= 0)");
        } else if (initialCapacity >= MAX_CAPACITY) {
            throw new IllegalArgumentException("Max initialCapacity need low than " + MAX_CAPACITY);
        }

        this.ihc = new IHC(this, new AtomicInteger(), log2Size(initialCapacity));
        this.val0 = 0;
    }

    // Convert to next largest power-of-2
    private static int log2Size(int minSize) {
        int log2;
        for (log2 = MIN_LOG_SIZE; (1L << log2) < minSize; log2++) {
            //empty
        }
        return log2;
    }

    private boolean CAS(final long offset, final Object oldObj, final Object newObj) {
        return UNSAFE.compareAndSwapObject(this, offset, oldObj, newObj);
    }

    private void helpCopy() {
        // Read the top-level IHC only once.  We'll try to help this copy along,
        // even if it gets promoted out from under us (i.e., the copy completes
        // and another KVS becomes the top-level copy).
        final IHC topIhc = ihc;
        if (topIhc.nextIhc == null) {
            return; // No copy in-progress
        }
        topIhc.helpCopy(false);
    }

    @Override
    public int get(int key) {
        if (key == NO_KEY) {
            return val0;
        }

        final int V = ihc.get(key);
        assert !isPrime(V); // Never return a Prime
        return V;
    }

    @Override
    public int getAndIncrement(int key) {
        return getAndAdd(key, 1);
    }

    @Override
    public int getAndAdd(int key, int delta) {
        return addDelta(key, delta);
    }

    private int addDelta(int key, int delta) {
        if (key == NO_KEY) {
            return UnsafeUtils.getAndAddInt(this, VAL_0_OFFSET, delta);
        }

        final int res = ihc.addDelta(key, delta, false);
        assert !isPrime(res);
        return res;
    }

    @Override
    public int incrementAndGet(int key) {
        return addAndGet(key, 1);
    }

    @Override
    public int addAndGet(int key, int delta) {
        return addDelta(key, delta) + delta;
    }

    @Override
    public int size() {
        return (val0 == 0 ? 0 : 1) + ihc.size();
    }

    @Override
    public void reset() {
        this.val0 = 0;
        finishCopy().reset();
    }

    @Override
    public long fillSortedKvs(LongBuf longBuf) {
        long totalCount = 0L;
        final int offset = longBuf.writerIndex();
        if (val0 > 0) {
            longBuf.write(0, val0);
            totalCount += val0;
        }

        final IHC topIhc = finishCopy();
        final int[] kvs = topIhc.kvs;
        for (int k = 0, len = kvs.length; k < len; k += 2) {
            final int value = kvs[k + 1];
            if (value > 0) {
                longBuf.write(kvs[k], value);
                totalCount += value;
            }
        }

        if (offset == longBuf.writerIndex()) {
            return 0;
        }

        final int writerIndex = longBuf.writerIndex();
        Arrays.sort(longBuf._buf(), offset, writerIndex);
        return totalCount;
    }

    private IHC finishCopy() {
        while (true) {
            final IHC topIhc = ihc;
            if (topIhc.nextIhc == null) { // No table-copy-in-progress
                return topIhc;
            }

            // Table copy in-progress - so we cannot get a clean iteration.  We
            // must help finish the table copy before we can start iterating.
            topIhc.helpCopy(true);
        }
    }

    private static final class IHC implements Serializable {

        private static final long serialVersionUID = -156965978265678243L;

        private static final AtomicReferenceFieldUpdater<IHC, IHC> NEXT_IHC_UPDATER =
                newUpdater(IHC.class, IHC.class, "nextIhc");

        private static final AtomicLongFieldUpdater<IHC> RESIZE_THREADS_UPDATER =
                newUpdater(IHC.class, "resizeThreads");

        private static final AtomicLongFieldUpdater<IHC> COPY_DONE_UPDATER = newUpdater(IHC.class, "copyDone");

        private static final AtomicLongFieldUpdater<IHC> COPY_IDX_UPDATER = newUpdater(IHC.class, "copyIdx");

        // Back-pointer to top-level structure
        private final AtomicIntHashCounter aihc;

        // Size in active K,V pairs
        private final AtomicInteger size;

        private final AtomicInteger slots;

        private volatile IHC nextIhc;

        private volatile long resizeThreads; // Count of threads attempting an initial resize

        private volatile long copyDone;

        private volatile long copyIdx;

        private final int[] kvs;

        private final int len;

        private final int reProbeLimit;

        IHC(AtomicIntHashCounter aihc, AtomicInteger size, int logSize) {
            this.aihc = aihc;
            this.size = size;
            this.slots = new AtomicInteger(0);
            this.kvs = new int[(1 << logSize) << 1];
            this.len = len(this.kvs);
            this.reProbeLimit = reProbeLimit(this.len);
        }

        public void reset() {
            UNSAFE.setMemory(kvs, byteOffset(0), ((long) kvs.length) * I_SCALE, (byte) 0);
            this.nextIhc = null;
            this.size.set(0);
            this.slots.set(0);
            this.resizeThreads = 0L;
            this.copyDone = 0L;
            this.copyIdx = 0L;
        }

        public int size() {
            return size.get();
        }

        private boolean casNextIhc(IHC newIhc) {
            return NEXT_IHC_UPDATER.compareAndSet(this, null, newIhc);
        }

        private boolean casKv(int idx, long oldKv, long newKv) {
            return UNSAFE.compareAndSwapLong(kvs, byteOffset(idx << 1), oldKv, newKv);
        }

        private boolean casValue(int idx, int oldVal, int newVal) {
            return UNSAFE.compareAndSwapInt(kvs, byteOffset((idx << 1) + 1), oldVal, newVal);
        }

        private long getKv(int idx) {
            return UNSAFE.getLongVolatile(kvs, byteOffset(idx << 1));
        }

        private int getValue(int idx) {
            return UNSAFE.getIntVolatile(kvs, byteOffset((idx << 1) + 1));
        }

        private int get(final int key) {
            final int lenMask = len - 1;
            int idx = key & lenMask; // First key hash

            long kv;
            int k, reProbeTimes = 0;
            while (true) {
                if ((kv = getKv(idx)) == 0L) {
                    return 0; // A clear miss
                }

                if ((k = key(kv)) == key) { // Key hit!
                    // Check for no table-copy-in-progress
                    final int v = value(kv);
                    if (!isPrime(v)) { //No copy?
                        return v;
                    }

                    aihc.helpCopy();
                    return nextIhc.get(key); // Retry in the new table
                }

                if (k == TOMB_PRIME) {
                    aihc.helpCopy();
                    return nextIhc.get(key); // Retry in the new table
                }

                if (++reProbeTimes >= reProbeLimit) {
                    if (nextIhc != null) { // Table copy in progress?
                        aihc.helpCopy();
                        return nextIhc.get(key); // Retry in the new table
                    }
                    return 0; // A clear miss
                }

                idx = (idx + 1) & lenMask; // re-probe by 1!
            }
        }

        private int addDelta(final int key, final int delta, final boolean fromTableCopy) {
            assert key > 0 && delta > 0;
            final int lenMask = len - 1;
            int idx = key & lenMask; // First key hash

            long kv;
            int k, v, reProbeTimes = 0;
            while (true) {
                kv = getKv(idx);
                k = key(kv);
                v = value(kv);
                if (k == NO_KEY) { //No key!
                    if (casKv(idx, kv, kv(key, delta))) {
                        slots.addAndGet(1);
                        if (!fromTableCopy) {
                            size.addAndGet(1);
                        }
                        return v;
                    }

                    kv = getKv(idx); // CAS failed, get updated value
                    k = key(kv);
                    v = value(kv);
                    assert k != NO_KEY;  // If keys[idx] is NO_KEY, CAS should work
                }

                if (k == key) {
                    while (true) {
                        // If a Prime'd value got installed, we need to re-run the addDelta on the new table.
                        if (isPrime(v)) { // Simply retry from the start.
                            if (!fromTableCopy) {
                                aihc.helpCopy(); // help along an existing copy
                            }
                            return nextIhc.addDelta(key, delta, v != TOMB_PRIME);
                        }

                        // See if we want to move to a new table (to avoid high average re-probe counts).
                        // We only check on the initial set of a Value from zero to not-zero
                        if (v == 0 && slots.get() >= len >> 1) {
                            final IHC newIhc = resize(); // Force the new table copy to start
                            if (!fromTableCopy) {
                                aihc.helpCopy(); // help along an existing copy
                            }
                            return newIhc.addDelta(key, delta, fromTableCopy);
                        }

                        // Try to increase the value with delta
                        if (casValue(idx, v, v + delta)) {
                            return v;
                        }

                        // CAS failed, get updated value
                        v = getValue(idx);
                    }
                }

                if (++reProbeTimes >= reProbeLimit || k == TOMB_PRIME) {
                    final IHC newIhc = resize();
                    if (!fromTableCopy) {
                        aihc.helpCopy(); // help along an existing copy
                    }
                    return newIhc.addDelta(key, delta, fromTableCopy);
                }
                idx = (idx + 1) & lenMask; // re-probe by 1!
            }
        }

        private IHC resize() {
            IHC newIhc = this.nextIhc;
            if (newIhc != null) {
                return newIhc;
            }

            final int newSize = this.len << 1;
            final int log2 = log2Size(newSize); // Convert to power-of-2

            // Prevent integer overflow - limit of 2^31 elements in a Java array.
            // So here, 2^30 is the largest number of elements in the hash table
            if (log2 > MAX_LOG_SIZE) {
                throw new RuntimeException("Table is full, size=" + size + ", newSize=" + newSize + ", log2=" + log2);
            }

            // Now limit the number of threads actually allocating memory to a
            // handful - lest we have 750 threads all trying to allocate a giant
            // resized array.
            long r = resizeThreads;
            while (!RESIZE_THREADS_UPDATER.compareAndSet(this, r, r + 1)) {
                r = resizeThreads;
            }

            // Size calculation: 2 words (K+V) per table entry, plus a handful.  We
            // guess at 64-bit pointers; 32-bit pointers screws up the size calc by
            // 2x but does not screw up the heuristic very much.
            final long megs = ((((1L << log2) << 1) + 8) << 3/*word to bytes*/) >> 20/*megs*/;
            if (r >= 2 && megs > 0) { // Already 2 guys trying; wait and see
                if ((newIhc = nextIhc) != null) {
                    return newIhc;
                }

                // We could use a wait with timeout, so we'll wake up as soon as the new table
                // is ready, or after the timeout in any case.
                // For now, sleep a tad and see if the 2 guys already trying to make
                // the table actually get around to making it happen.
                try {
                    Thread.sleep(megs);
                } catch (Exception e) {
                    //ignore
                }
            }

            // Last check, since the 'new' below is expensive and there is a chance
            // that another thread slipped in a new thread while we ran the heuristic.
            if ((newIhc = nextIhc) != null) { // See if resize is already in progress
                return newIhc; // Use the new table already
            }

            // New IHC - actually allocate the big arrays
            newIhc = new IHC(aihc, this.size, log2);

            // Another check after the slow allocation
            if (nextIhc != null) { // See if resize is already in progress
                return nextIhc; // Use the new table already
            }

            // The new table must be CAS'd in so only 1 winner amongst duplicate racing resizing threads.
            if (casNextIhc(newIhc)) { // NOW a resize-is-in-progress!
                return newIhc;
            } else { // CAS failed?
                return nextIhc; // Reread new table
            }
        }

        private void helpCopy(final boolean copyAll) {
            final IHC newIhc = this.nextIhc;
            assert newIhc != null;    // Already checked by caller
            final int oldLen = this.len; // Total amount to copy
            final int MIN_COPY_WORK = Math.min(oldLen, 1024); // Limit per-thread work
            int panicStart = -1, copyIdx = -9999;
            while (copyDone < oldLen) { // Still needing to copy?
                // Carve out a chunk of work.  The counter wraps around so every
                // thread eventually tries to copy every slot repeatedly.

                // We "panic" if we have tried TWICE to copy every slot - and it still
                // has not happened.  i.e., twice some thread somewhere claimed they
                // would copy 'slot X' (by bumping _copyIdx) but they never claimed to
                // have finished (by bumping _copyDone).  Our choices become limited:
                // we can wait for the work-claimers to finish (and become a blocking
                // algorithm) or do the copy work ourselves.  Tiny tables with huge
                // thread counts trying to copy the table often 'panic'.
                if (panicStart == -1) { // No panic?
                    copyIdx = (int) this.copyIdx;
                    while (!COPY_IDX_UPDATER.compareAndSet(this, copyIdx, copyIdx + MIN_COPY_WORK)) {
                        copyIdx = (int) this.copyIdx;     // Re-read
                    }

                    if (!(copyIdx < (oldLen << 1))) { // Panic!
                        panicStart = copyIdx; // Record where we started to panic-copy
                    }
                }

                // We now know what to copy.  Try to copy.
                int workDone = 0;
                for (int i = 0; i < MIN_COPY_WORK; i++) {
                    if (copySlot((copyIdx + i) & (oldLen - 1))) { // Made an old-table slot go dead?
                        workDone++; // Yes!
                    }
                }

                if (workDone > 0) { // Report work-done occasionally
                    copyCheckAndPromote(workDone); // See if we can promote
                }

                copyIdx += MIN_COPY_WORK;

                // Uncomment these next 2 lines to turn on incremental table-copy.
                // Otherwise, this thread continues to copy until it is all done.
                if (!copyAll && panicStart == -1) { // No panic?
                    return; // Then done copying after doing MIN_COPY_WORK
                }
            }

            // Extra promotion check, in case another thread finished all copying then got stalled before promoting.
            copyCheckAndPromote(0); // See if we can promote
        }

        private boolean copySlot(int idx) {
            // Prevent new values from appearing in the old table.
            // Slap a TOMB_PRIME down in the old table, to prevent further updates.
            long kv = getKv(idx);
            int oldKey = key(kv);
            int oldVal = value(kv);
            while (!isPrime(oldVal)) {
                final int box = oldVal == 0 ? TOMB_PRIME : boxPrime(oldVal);
                if (casKv(idx, kv, kv(oldKey == 0 ? TOMB_PRIME : oldKey, box))) {
                    if (oldVal != 0) {
                        // We need to copy oldVal to the new table.
                        nextIhc.addDelta(oldKey, oldVal, true);
                    }
                    return true;
                }

                kv = getKv(idx); // Read OLD table
                oldKey = key(kv);
                oldVal = value(kv);
            }
            return false;
        }

        private void copyCheckAndPromote(int workDone) {
            final int oldLen = this.len;
            // We made a slot unusable and so did some needed copy work
            long copyDone = this.copyDone;
            assert (copyDone + workDone) <= oldLen;
            if (workDone > 0) {
                while (!COPY_DONE_UPDATER.compareAndSet(this, copyDone, copyDone + workDone)) {
                    copyDone = this.copyDone; // Reload, retry
                    assert (copyDone + workDone) <= oldLen;
                }
            }

            // Check for copy being ALL done, and promote.  Note that we might have
            // nested in-progress copies and manage to finish a nested copy before
            // finishing the top-level copy.  We only promote top-level copies.
            if (copyDone + workDone == oldLen && // Ready to promote this table?
                    aihc.ihc == this) {        // Looking at the top-level table?
                aihc.CAS(IHC_OFFSET, this, nextIhc);
            }
        }
    }

    private static long byteOffset(int idx) {
        return ((long) idx << I_SHIFT) + I_BASE;
    }

    private static long kv(int key, int value) {
        return ((long) value) << 32 | key;
    }

    private static int key(long kvLong) {
        return (int) kvLong;
    }

    private static int value(long kvLong) {
        return (int) (kvLong >> 32);
    }

    private static boolean isPrime(int v) {
        return (v & PRIME_MASK) != 0;
    }

    private static int boxPrime(int v) {
        return v | PRIME_MASK;
    }

    private static int reProbeLimit(int len) {
        return RE_PROBE_LIMIT + (len >> 4);
    }

    private static int len(int[] kvs) {
        return kvs.length >> 1;
    }
}
