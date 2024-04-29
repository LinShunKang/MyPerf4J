package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArrayV1;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArrayV0;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArrayV2;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.atomic.AtomicIntegerArray;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;

/**
 * Created by LinShunkang on 2024/02/15
 */
@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
public abstract class AbstractAtomicIntArrayBench {

    protected AtomicIntegerArray jdkIntArray;

    protected AtomicIntArray atomicIntArray;

    protected FastAtomicIntArrayV0 fastAtomicIntArrayV0;

    protected FastAtomicIntArrayV1 fastAtomicIntArrayV1;

    protected FastAtomicIntArrayV2 fastAtomicIntArrayV2;

    @Setup
    public void setup() {
        final int length = arrayLength();
        jdkIntArray = new AtomicIntegerArray(length);
        atomicIntArray = new AtomicIntArray(length);
        fastAtomicIntArrayV0 = new FastAtomicIntArrayV0(length, 16);
        fastAtomicIntArrayV1 = new FastAtomicIntArrayV1(length, 16);
        fastAtomicIntArrayV2 = new FastAtomicIntArrayV2(length, 16);
    }

    abstract int arrayLength();

    @State(Scope.Thread)
    public static class ThreadState {

        private final SimpleRandom random = new SimpleRandom();

        int next() {
            return random.next();
        }
    }
}
