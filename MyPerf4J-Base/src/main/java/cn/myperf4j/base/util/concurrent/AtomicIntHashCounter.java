package cn.myperf4j.base.util.concurrent;

/**
 * Created by LinShunkang on 2022/03/19
 */
public interface AtomicIntHashCounter {

    int get(int key);

    int incrementAndGet(int key);

    int addAndGet(int key, int delta);

    int size();

    void reset();
}
