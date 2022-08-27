package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.IntHashCounter;
import cn.myperf4j.base.util.concurrent.AtomicIntHashCounter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2022/03/20
 * <p>
 * # JMH version: 1.34
 * # VM version: JDK 17.0.4, Java HotSpot(TM) 64-Bit Server VM, 17.0.4+11-LTS-179
 * # VM invoker: /Library/Java/JavaVirtualMachines/jdk-17.0.4.jdk/Contents/Home/bin/java
 * # VM options: -ea -Xmx8G -Xms8G -Xmn4G -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=52807:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
 * # Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
 * # Warmup: 1 iterations, 10 s each
 * # Measurement: 5 iterations, 10 s each
 * # Timeout: 10 min per iteration
 * # Benchmark mode: Throughput, ops/time
 * # Parameters: (mapSize = 1048576)
 * <p>
 * # Threads: 1 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score    Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5  241.139 ±  2.975  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5   13.969 ±  3.741  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5  184.314 ± 32.741  ops/us
 * <p>
 * # Threads: 2 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score    Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5  375.245 ± 23.964  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5   33.437 ±  7.945  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5  327.925 ± 44.565  ops/us
 * <p>
 * # Threads: 4 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score    Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5  532.269 ± 21.093  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5   67.699 ±  2.826  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5  503.602 ± 61.535  ops/us
 * <p>
 * # Threads: 8 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score   Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5   86.101 ± 8.562  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5  133.705 ± 3.813  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5   86.922 ± 6.591  ops/us
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class AtomicIntHashCounterBench {

    private IntHashCounter intHashCounter;

    private AtomicIntArray intArray;

    private ConcurrentMap<Integer, AtomicInteger> jdkIntegerMap;

    @Param(value = {"1048576"})
    private int mapSize;

    @Setup(Level.Iteration)
    public void setup() {
        intHashCounter = new AtomicIntHashCounter(128);
        intArray = new AtomicIntArray(mapSize + 1);
        jdkIntegerMap = new ConcurrentHashMap<>(128);

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 1; i < mapSize; i++) {
            final int key = random.nextInt(0, mapSize);
            intHashCounter.incrementAndGet(key);
            intArray.incrementAndGet(key);
            increase(jdkIntegerMap, key);
        }
    }

    private int randomKey(ThreadState state) {
        return state.next() & (mapSize - 1);
    }

    @Benchmark
    public int scalableIntHashCounter(ThreadState state) {
        return intHashCounter.incrementAndGet(randomKey(state));
    }

    @Benchmark
    public int intArray(ThreadState state) {
        return intArray.incrementAndGet(randomKey(state));
    }

    @Benchmark
    public int jdkIntegerMap(ThreadState state) {
        return increase(jdkIntegerMap, randomKey(state));
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
                .forks(1)
                .threads(1)
                .warmupIterations(1)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }

    @State(Scope.Thread)
    public static class ThreadState {

        private final SimpleRandom random = new SimpleRandom();

        int next() {
            return random.next();
        }
    }
}
