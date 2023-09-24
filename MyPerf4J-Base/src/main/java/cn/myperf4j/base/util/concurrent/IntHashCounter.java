package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;

/**
 * Created by LinShunkang on 2022/03/19
 */
public interface IntHashCounter {

    /**
     * Get the specified value of the specified key.
     *
     * @param key the key
     * @return the specified value of the specified key, or 0 if this map contains no mapping for the key
     */
    int get(int key);

    /**
     * Atomically increments the specified value of the specified key by one.
     *
     * @param key the key
     * @return the previous value. Negative return indicates that update failure.
     */
    int getAndIncrement(int key);

    /**
     * Atomically adds the given value of the specified key to the specified value.
     *
     * @param key the key
     * @return the previous value. Negative return indicates that update failure.
     */
    int getAndAdd(int key, int delta);

    /**
     * Atomically increments the specified value of the specified key by one.
     *
     * @param key the key
     * @return the updated value
     */
    int incrementAndGet(int key);

    /**
     * Atomically adds the given value of the specified key to the specified value.
     *
     * @param key the key
     * @return the updated value. Non-positive number return indicates that update failure.
     */
    int addAndGet(int key, int delta);

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    int size();

    /**
     * Removes all the mappings from this map (optional operation).
     */
    void reset();

    /**
     * Write sorted key-value into longBuf, order by key.
     *
     * @param longBuf the LongBuf
     * @return the total values write into longBuf
     */
    long fillSortedKvs(LongBuf longBuf);
}
