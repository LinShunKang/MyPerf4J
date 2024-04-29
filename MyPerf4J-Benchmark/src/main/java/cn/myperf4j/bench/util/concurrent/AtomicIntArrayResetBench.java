package cn.myperf4j.bench.util.concurrent;

import cn.myperf4j.base.util.concurrent.AtomicIntArray;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArrayV0;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArrayV1;
import cn.myperf4j.base.util.concurrent.FastAtomicIntArrayV2;
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
 * JMH version: 1.37
 * VM version: JDK 1.8.0_362, OpenJDK 64-Bit Server VM, 25.362-b09
 * VM invoker: /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/bin/java
 * VM options: -server -Xmx8G -Xms8G
 * <p>
 * Benchmark                                                (arrayLength)   Mode  Cnt  Score    Error   Units
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                  128  thrpt    3  8.975 ±  1.368  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                  256  thrpt    3  7.104 ±  0.659  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                  512  thrpt    3  5.483 ±  0.506  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                 1024  thrpt    3  3.780 ±  0.221  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                 2048  thrpt    3  2.338 ±  0.077  ops/us
 * AtomicIntArrayResetBench.atomicIntArrayResetBench                 4096  thrpt    3  1.327 ±  0.046  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV0ResetBench            128  thrpt    3  0.136 ±  0.014  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV0ResetBench            256  thrpt    3  0.082 ±  0.007  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV0ResetBench            512  thrpt    3  0.044 ±  0.002  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV0ResetBench           1024  thrpt    3  0.023 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV0ResetBench           2048  thrpt    3  0.012 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV0ResetBench           4096  thrpt    3  0.006 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV1ResetBench            128  thrpt    3  0.190 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV1ResetBench            256  thrpt    3  0.096 ±  0.009  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV1ResetBench            512  thrpt    3  0.048 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV1ResetBench           1024  thrpt    3  0.024 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV1ResetBench           2048  thrpt    3  0.012 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV1ResetBench           4096  thrpt    3  0.006 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV2ResetBench            128  thrpt    3  0.190 ±  0.002  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV2ResetBench            256  thrpt    3  0.096 ±  0.003  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV2ResetBench            512  thrpt    3  0.048 ±  0.002  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV2ResetBench           1024  thrpt    3  0.024 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV2ResetBench           2048  thrpt    3  0.012 ±  0.001  ops/us
 * AtomicIntArrayResetBench.fastAtomicIntArrayV2ResetBench           4096  thrpt    3  0.006 ±  0.001  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                     128  thrpt    3  9.968 ±  0.505  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                     256  thrpt    3  5.251 ±  0.374  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                     512  thrpt    3  2.691 ±  0.220  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                    1024  thrpt    3  1.363 ±  0.148  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                    2048  thrpt    3  0.684 ±  0.113  ops/us
 * AtomicIntArrayResetBench.jdkIntArrayResetBench                    4096  thrpt    3  0.343 ±  0.069  ops/us
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
    public int fastAtomicIntArrayV0ResetBench() {
        final FastAtomicIntArrayV0 myArray = this.fastAtomicIntArrayV0;
        myArray.reset();
        return myArray.length();
    }

    @Benchmark
    public int fastAtomicIntArrayV1ResetBench() {
        final FastAtomicIntArrayV1 myArray = this.fastAtomicIntArrayV1;
        myArray.reset();
        return myArray.length();
    }

    @Benchmark
    public int fastAtomicIntArrayV2ResetBench() {
        final FastAtomicIntArrayV2 myArray = this.fastAtomicIntArrayV2;
        myArray.reset();
        return myArray.length();
    }

    public static void main(String[] args) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(AtomicIntArrayResetBench.class.getSimpleName())
                .jvmArgs("-server", "-Xmx8G", "-Xms8G")
                .forks(1)
                .threads(1)
                .warmupIterations(1)
                .measurementIterations(3)
                .build();
        new Runner(opt).run();
    }
}
