package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.SimpleAtomicIntArray;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArray;
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

    protected AtomicIntArray simpleAtomicIntArray;

    protected AtomicIntArray fastAtomicIntArray;

    @Setup
    public void setup() {
        final int length = arrayLength();
        jdkIntArray = new AtomicIntegerArray(length);
        simpleAtomicIntArray = new SimpleAtomicIntArray(length);
        fastAtomicIntArray = new FastAtomicIntArray(length, 16);
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
