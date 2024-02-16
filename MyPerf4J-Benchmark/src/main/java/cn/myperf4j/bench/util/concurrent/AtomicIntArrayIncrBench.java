package cn.myperf4j.bench.util.concurrent;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Created by LinShunkang on 2024/02/15
 * # JMH version: 1.37
 * # VM version: JDK 1.8.0_321, Java HotSpot(TM) 64-Bit Server VM, 25.321-b07
 * # VM options: -Xmx8G
 * <p>
 * 1 Thread
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3  187.695 ± 206.204  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3  190.066 ±  95.425  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  190.068 ±  93.475  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  187.959 ± 200.285  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  191.378 ± 111.212  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  190.573 ±  97.597  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  169.209 ±  60.033  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  169.203 ±  57.655  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  166.773 ±  65.460  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  164.579 ± 217.782  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  157.130 ± 115.277  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  154.180 ±  43.882  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3  216.603 ± 296.491  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3  201.221 ± 203.104  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  219.170 ±  80.523  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  192.776 ± 335.080  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  199.834 ± 223.958  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  219.634 ±  86.488  ops/us
 * <p>
 * 2 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3   76.431 ±  29.993  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3   92.370 ±  48.204  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  101.579 ±  49.549  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  112.608 ±  74.858  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  119.524 ±  69.509  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  124.977 ±  47.791  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  327.004 ±  90.724  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  323.593 ± 105.444  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  327.442 ± 119.052  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  328.704 ± 116.771  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  324.819 ±  98.129  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  290.689 ± 394.473  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3   78.342 ±  26.449  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3   93.028 ±  30.171  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  104.552 ±  11.239  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  116.050 ±  40.671  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  121.776 ±  47.691  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  125.901 ±  64.371  ops/us
 * <p>
 * 4 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3   78.325 ±  15.654  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3  106.452 ±   7.067  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  129.881 ±  15.240  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  143.532 ±  42.835  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  163.093 ±  48.837  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  170.398 ±  61.696  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  629.628 ± 200.550  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  642.708 ± 234.968  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  556.982 ± 106.658  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  631.829 ± 245.471  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  628.525 ± 228.230  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  588.105 ± 200.849  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3   79.450 ±  10.203  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3  105.685 ±  27.657  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  126.899 ±  25.018  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  151.160 ±  59.103  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  163.772 ±  44.476  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  168.028 ±  42.372  ops/us
 * <p>
 * 8 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt     Score      Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3    42.412 ±    3.462  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3    50.267 ±    3.508  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3    64.850 ±    4.982  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3    81.494 ±    8.277  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3    93.750 ±    9.145  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3   109.582 ±   14.970  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  1148.289 ±  319.312  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  1172.098 ±  831.775  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  1090.070 ±  214.388  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  1169.325 ±  802.631  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  1189.624 ± 1606.922  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  1163.776 ±  306.020  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3    43.310 ±    3.064  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3    53.476 ±    3.942  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3    72.339 ±   11.886  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3    86.566 ±    6.782  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3    97.097 ±    8.456  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3   106.443 ±    2.279  ops/us
 */
@State(Scope.Benchmark)
public class AtomicIntArrayIncrBench extends AbstractAtomicIntArrayBench {

    @Param(value = {"128", "256", "512", "1024", "2048", "4096"})
    private int arrayLength;

    @Override
    int arrayLength() {
        return arrayLength;
    }

    @Benchmark
    public int jdkIntArrayBench(ThreadState state) {
        return jdkIntArray.incrementAndGet(randomKey(state));
    }

    private int randomKey(ThreadState state) {
        return state.next() & (arrayLength - 1);
    }

    @Benchmark
    public int atomicIntArrayBench(ThreadState state) {
        return atomicIntArray.incrementAndGet(randomKey(state));
    }

    @Benchmark
    public boolean fastAtomicIntArrayBench(ThreadState state) {
        return fastAtomicIntArray.increment(randomKey(state));
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行3遍warmup，然后执行5遍测试
        final Options opt = new OptionsBuilder()
                .include(AtomicIntArrayIncrBench.class.getSimpleName())
                .jvmArgs("-Xmx8G")
                .forks(1)
                .threads(1)
                .warmupIterations(1)
                .measurementIterations(3)
                .build();
        new Runner(opt).run();
    }
}
