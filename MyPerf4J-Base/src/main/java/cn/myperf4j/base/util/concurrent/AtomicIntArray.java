package cn.myperf4j.base.util.concurrent;

import cn.myperf4j.base.buffer.LongBuf;

/**
 * Created by LinShunkang on 2024/06/16
 */
public interface AtomicIntArray {

    int length();

    int get(int index);

    int getAndIncrement(int index);

    int getAndAdd(int index, int delta);

    int incrementAndGet(int index);

    int addAndGet(int index, int delta);

    long fillSortedKvs(LongBuf longBuf);

    void reset();
}
