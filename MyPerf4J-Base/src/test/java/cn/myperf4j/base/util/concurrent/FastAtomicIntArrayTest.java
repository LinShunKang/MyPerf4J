package cn.myperf4j.base.util.concurrent;

/**
 * Created by LinShunkang on 2024/02/14
 */
public final class FastAtomicIntArrayTest extends AtomicIntArrayTest {

    @Override
    public AtomicIntArray createInstance() {
        return new FastAtomicIntArray(1024);
    }
}
