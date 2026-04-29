package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.atomic.AtomicIntegerArray;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;

/**
 * Created by LinShunkang on 2020/11/24
 */
@Threads(value = 8)
@State(Scope.Thread)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
@Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
@Fork(value = 2, jvmArgs = {"-server", "-Xmx8G", "-Xms8G", "-Xmn4G"})
public class AtomicIntArrayBench {

    private AtomicIntegerArray jdkArray;

    private AtomicIntArray myArray;

    @Setup
    public void setup() {
        jdkArray = new AtomicIntegerArray(1024);
        myArray = new AtomicIntArray(1024);
    }

    @Benchmark
    public int jdkArrayBench() {
        return jdkArray.getAndAdd(1, 1);
    }

    @Benchmark
    public int myArrayBench() {
        return myArray.getAndAdd(1, 1);
    }

    @Benchmark
    public int jdkArrayResetBench() {
        final AtomicIntegerArray jdkArray = this.jdkArray;
        for (int i = 0, length = jdkArray.length(); i < length; ++i) {
            jdkArray.set(i, 0);
        }
        return jdkArray.length();
    }

    @Benchmark
    public int myArrayResetBench() {
        final AtomicIntArray myArray = this.myArray;
        myArray.reset();
        return myArray.length();
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(AtomicIntArrayBench.class.getSimpleName()).build()).run();
    }
}
