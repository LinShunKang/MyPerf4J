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
 * Benchmark                                              (arrayLength)   Mode  Cnt  Score    Error   Units
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                128  thrpt    3  8.993 ±  1.491  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                256  thrpt    3  7.131 ±  0.820  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                512  thrpt    3  5.492 ±  0.520  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench               1024  thrpt    3  3.778 ±  0.332  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench               2048  thrpt    3  2.340 ±  0.152  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench               4096  thrpt    3  1.344 ±  0.023  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench            128  thrpt    3  0.140 ±  0.006  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench            256  thrpt    3  0.080 ±  0.002  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench            512  thrpt    3  0.044 ±  0.004  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench           1024  thrpt    3  0.023 ±  0.002  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench           2048  thrpt    3  0.012 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayResetBench           4096  thrpt    3  0.006 ±  0.001  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                   128  thrpt    3  9.977 ±  0.082  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                   256  thrpt    3  5.255 ±  0.223  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                   512  thrpt    3  2.691 ±  0.228  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                  1024  thrpt    3  1.363 ±  0.152  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                  2048  thrpt    3  0.685 ±  0.095  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                  4096  thrpt    3  0.344 ±  0.052  ops/us
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
