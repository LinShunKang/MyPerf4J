package cn.myperf4j.base.util.concurrent;

/**
 * Created by LinShunkang on 2020/11/24
 */
public final class SimpleAtomicIntArrayTest extends AtomicIntArrayTest {

    @Override
    public AtomicIntArray createInstance() {
        return new SimpleAtomicIntArray(1024);
    }
}
