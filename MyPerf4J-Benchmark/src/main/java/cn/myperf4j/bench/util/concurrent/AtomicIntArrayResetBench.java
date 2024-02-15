package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArray;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2024/02/15
 * <p>
 * # JMH version: 1.37
 * # VM version: JDK 1.8.0_321, Java HotSpot(TM) 64-Bit Server VM, 25.321-b07
 * # VM options: -Xmx8G
 * <p>
 * Benchmark                                              (arrayLength)   Mode  Cnt   Score    Error   Units
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                128  thrpt    3  14.670 ±  0.858  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                256  thrpt    3  10.563 ±  0.281  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                512  thrpt    3   7.066 ±  0.291  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench               1024  thrpt    3   4.510 ±  0.136  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench               2048  thrpt    3   2.591 ±  0.569  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench               4096  thrpt    3   1.431 ±  0.014  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench            128  thrpt    3   2.627 ±  0.232  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench            256  thrpt    3   1.421 ±  0.115  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench            512  thrpt    3   0.744 ±  0.018  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench           1024  thrpt    3   0.381 ±  0.002  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench           2048  thrpt    3   0.192 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench           4096  thrpt    3   0.097 ±  0.001  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                   128  thrpt    3   1.129 ±  0.065  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                   256  thrpt    3   0.566 ±  0.019  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                   512  thrpt    3   0.283 ±  0.004  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                  1024  thrpt    3   0.142 ±  0.003  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                  2048  thrpt    3   0.071 ±  0.001  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                  4096  thrpt    3   0.035 ±  0.001  ops/us
 */
@State(Scope.Thread)
public class AtomicIntArrayResetBench extends AbstractAtomicIntArrayBench {

    @Param(value = {"128", "256", "512", "1024", "2048", "4096"})
    private int arrayLength;

    @Override
    int arrayLength() {
        return arrayLength;
    }

    @Benchmark
    public int jdkIntArrayResetBench() {
        final AtomicIntegerArray jdkArray = this.jdkIntArray;
        for (int i = 0, length = jdkArray.length(); i < length; ++i) {
            jdkArray.set(i, 0);
        }
        return jdkArray.length();
    }

    @Benchmark
    public int atomicIntArrayResetBench() {
        final AtomicIntArray intArray = this.atomicIntArray;
        intArray.reset();
        return intArray.length();
    }

    @Benchmark
    public int fastAtomicIntArrayResetBench() {
        final FastAtomicIntArray myArray = this.fastAtomicIntArray;
        myArray.reset();
        return myArray.length();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(AtomicIntArrayResetBench.class.getSimpleName())
                .jvmArgs("-Xmx8G")
                .forks(1)
                .threads(1)
                .warmupIterations(1)
                .measurementIterations(3)
                .build();
        new Runner(opt).run();
    }
}
