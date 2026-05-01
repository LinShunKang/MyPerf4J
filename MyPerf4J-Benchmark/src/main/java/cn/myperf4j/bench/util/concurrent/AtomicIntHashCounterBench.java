package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntHashCounter;
import cn.myperf4j.base.util.concurrent.IntHashCounter;
import cn.myperf4j.base.util.concurrent.SimpleAtomicIntArray;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;

/**
 * Created by LinShunkang on 2022/03/20
 * <p>
 * # CPU: Apple M4 Max
 * # JMH version: 1.37
 * # VM version: JDK 17.0.12, Java HotSpot(TM) 64-Bit Server VM, 17.0.12+8-LTS-286
 * # VM invoker: /Library/Java/JavaVirtualMachines/jdk-17.0.4.jdk/Contents/Home/bin/java
 * # VM options: -server -Xmx8G -Xms8G -Xmn4G
 * # Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
 * # Warmup: 1 iterations, 10 s each
 * # Measurement: 5 iterations, 10 s each
 * # Timeout: 10 min per iteration
 * # Benchmark mode: Throughput, ops/time
 * # Parameters: (mapSize = 1048576)
 * <p>
 * # Threads: 1 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score    Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5  468.846 ±  1.929  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5   51.444 ± 12.695  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5  295.312 ±  7.982  ops/us
 * <p>
 * # Threads: 2 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score    Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5  705.684 ± 11.277  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5   98.496 ± 22.287  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5  553.091 ± 43.590  ops/us
 * <p>
 * # Threads: 4 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score     Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5  879.251 ±  35.852  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5  161.563 ±  29.734  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5  820.666 ± 101.836  ops/us
 * <p>
 * # Threads: 8 thread
 * Benchmark                                         (mapSize)   Mode  Cnt    Score    Error   Units
 * AtomicIntHashCounterBench.intArray                  1048576  thrpt    5  283.531 ± 36.862  ops/us
 * AtomicIntHashCounterBench.jdkIntegerMap             1048576  thrpt    5  280.059 ± 35.053  ops/us
 * AtomicIntHashCounterBench.scalableIntHashCounter    1048576  thrpt    5  298.224 ± 95.668  ops/us
 */
@Threads(value = 1)
@State(Scope.Thread)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
@Fork(value = 2, jvmArgs = {"-server", "-Xmx8G", "-Xms8G", "-Xmn4G"})
public class AtomicIntHashCounterBench {

    private IntHashCounter intHashCounter;

    private SimpleAtomicIntArray intArray;

    private ConcurrentMap<Integer, AtomicInteger> jdkIntegerMap;

    @Param(value = {"1048576"})
    private int mapSize;

    @Setup(Level.Iteration)
    public void setup() {
        intHashCounter = new AtomicIntHashCounter(128);
        intArray = new SimpleAtomicIntArray(mapSize + 1);
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
        new Runner(new OptionsBuilder().include(AtomicIntHashCounterBench.class.getSimpleName()).build()).run();
    }

    @State(Scope.Thread)
    public static class ThreadState {

        private final SimpleRandom random = new SimpleRandom();

        int next() {
            return random.next();
        }
    }
}
