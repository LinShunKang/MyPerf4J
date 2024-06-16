package cn.myperf4j.base.util.concurrent;

import org.junit.Before;

/**
 * Created by LinShunkang on 2024/02/14
 */
public final class FastAtomicIntArrayTest extends AtomicIntArrayTest {

    @Before
    public void clear() {
        atomicIntArray = new FastAtomicIntArray(1024);
    }
}
