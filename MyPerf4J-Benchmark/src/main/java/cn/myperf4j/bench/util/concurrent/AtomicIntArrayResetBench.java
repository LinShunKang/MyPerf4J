package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.atomic.AtomicIntegerArray;

import static org.openjdk.jmh.runner.options.TimeValue.seconds;

/**
 * Created by LinShunkang on 2024/02/15
 * <p>
 * JMH version: 1.37
 * VM version: JDK 1.8.0_362, OpenJDK 64-Bit Server VM, 25.362-b09
 * VM invoker: /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/bin/java
 * VM options: -server -Xmx8G -Xms8G
 * <p>
 * Benchmark                                      (arrayLength)   Mode  Cnt   Score    Error   Units
 * AtomicIntArrayResetBench.fastAtomicIntArray              128  thrpt    5   0.191 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArray              256  thrpt    5   0.096 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArray              512  thrpt    5   0.048 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArray             1024  thrpt    5   0.024 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArray             2048  thrpt    5   0.012 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArray             4096  thrpt    5   0.006 ±  0.001  ops/us
 * AtomicIntArrayResetBench.jdkIntArray                     128  thrpt    5   1.125 ±  0.010  ops/us
 * AtomicIntArrayResetBench.jdkIntArray                     256  thrpt    5   0.563 ±  0.003  ops/us
 * AtomicIntArrayResetBench.jdkIntArray                     512  thrpt    5   0.282 ±  0.001  ops/us
 * AtomicIntArrayResetBench.jdkIntArray                    1024  thrpt    5   0.141 ±  0.002  ops/us
 * AtomicIntArrayResetBench.jdkIntArray                    2048  thrpt    5   0.071 ±  0.001  ops/us
 * AtomicIntArrayResetBench.jdkIntArray                    4096  thrpt    5   0.035 ±  0.001  ops/us
 * AtomicIntArrayResetBench.simpleAtomicIntArray            128  thrpt    5  14.674 ±  0.181  ops/us
 * AtomicIntArrayResetBench.simpleAtomicIntArray            256  thrpt    5  10.519 ±  0.166  ops/us
 * AtomicIntArrayResetBench.simpleAtomicIntArray            512  thrpt    5   7.024 ±  0.012  ops/us
 * AtomicIntArrayResetBench.simpleAtomicIntArray           1024  thrpt    5   4.499 ±  0.014  ops/us
 * AtomicIntArrayResetBench.simpleAtomicIntArray           2048  thrpt    5   2.613 ±  0.004  ops/us
 * AtomicIntArrayResetBench.simpleAtomicIntArray           4096  thrpt    5   1.417 ±  0.014  ops/us
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
    public int jdkIntArray() {
        final AtomicIntegerArray jdkArray = this.jdkIntArray;
        for (int i = 0, length = jdkArray.length(); i < length; ++i) {
            jdkArray.set(i, 0);
        }
        return jdkArray.length();
    }

    @Benchmark
    public int simpleAtomicIntArray() {
        final AtomicIntArray intArray = this.simpleAtomicIntArray;
        intArray.reset();
        return intArray.length();
    }

    @Benchmark
    public int fastAtomicIntArray() {
        final AtomicIntArray myArray = this.fastAtomicIntArray;
        myArray.reset();
        return myArray.length();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(AtomicIntArrayResetBench.class.getSimpleName())
                .jvmArgs("-server", "-Xmx8G", "-Xms8G")
                .forks(1)
                .threads(1)
                .warmupTime(seconds(3))
                .warmupIterations(1)
                .measurementTime(seconds(5))
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
