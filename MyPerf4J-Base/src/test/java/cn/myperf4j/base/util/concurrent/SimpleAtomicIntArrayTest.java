package cn.myperf4j.base.util.concurrent;

import org.junit.Before;

/**
 * Created by LinShunkang on 2020/11/24
 */
public final class SimpleAtomicIntArrayTest extends AtomicIntArrayTest {

    @Before
    public void clear() {
        atomicIntArray = new SimpleAtomicIntArray(1024);
    }
}
