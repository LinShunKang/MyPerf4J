package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.AtomicIntHashCounter;
import cn.myperf4j.base.util.concurrent.FixedAtomicIntHashCounter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2022/03/20
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class AtomicIntHashCounterBench {

    private AtomicIntHashCounter intHashCounter;

    private AtomicIntArray intArray;

    private ConcurrentMap<Integer, AtomicInteger> integerMap;

    private final int[] keys = {0, 8, 16, 24, 32, 40, 48, 56};

    @Setup
    public void setup() {
        intHashCounter = new FixedAtomicIntHashCounter(128);
        intArray = new AtomicIntArray(128);
        integerMap = new ConcurrentHashMap<>(128);
    }

    @Benchmark
    public int intHashCounter() {
        return intHashCounter.incrementAndGet(64);
    }

//    @Benchmark
    public int intArray() {
        return intArray.incrementAndGet(64);
    }

    @Benchmark
    public int integerMap() {
        return increase(integerMap, 64);
    }

    private int increase(ConcurrentMap<Integer, AtomicInteger> integerHashMap, int k) {
        final AtomicInteger count = integerHashMap.get(k);
        if (count != null) {
            return count.incrementAndGet();
        }

        final AtomicInteger oldCounter = integerHashMap.putIfAbsent(k, new AtomicInteger(1));
        if (oldCounter != null) {
            return oldCounter.incrementAndGet();
        }
        return 0;
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行3遍warmup，然后执行5遍测试
        final Options opt = new OptionsBuilder()
                .include(AtomicIntHashCounterBench.class.getSimpleName())
                .forks(2)
                .threads(Runtime.getRuntime().availableProcessors() / 2 + 1)
                .warmupIterations(3)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
