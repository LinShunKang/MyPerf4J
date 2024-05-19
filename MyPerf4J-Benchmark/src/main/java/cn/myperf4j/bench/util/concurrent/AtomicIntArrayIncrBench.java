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
 * # VM version: JDK 1.8.0_362, OpenJDK 64-Bit Server VM, 25.362-b09
 * # VM options: -server -Xmx8G -Xms8G
 * <p>
 * 1 Thread
 * Benchmark                                          (arrayLength)   Mode  Cnt    Score    Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5  251.988 ± 49.409  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5  255.029 ± 49.528  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5  254.072 ± 45.776  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5  255.810 ± 47.321  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5  253.602 ± 45.634  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5  255.569 ± 46.306  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  228.830 ± 30.665  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  229.781 ± 35.404  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  229.923 ± 35.284  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  230.052 ± 35.501  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  227.171 ± 34.714  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  203.357 ± 21.493  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5  232.807 ± 34.036  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5  207.315 ± 28.008  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5  204.140 ± 26.741  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5  202.140 ± 25.840  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5  199.852 ± 26.008  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5  191.945 ± 23.567  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  237.451 ± 42.799  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  237.054 ± 41.515  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  236.741 ± 46.657  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  238.788 ± 41.932  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  235.953 ± 38.979  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  211.477 ± 25.852  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5  252.769 ± 45.090  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5  254.999 ± 44.275  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5  255.801 ± 46.276  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5  252.776 ± 39.681  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5  252.784 ± 39.753  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5  255.969 ± 46.292  ops/us
 * <p>
 * 2 Threads
 * Benchmark                                          (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5   83.722 ±  16.996  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5   93.750 ±   5.558  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5   57.995 ± 165.770  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5   38.869 ± 127.714  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5  122.598 ±  10.269  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5  127.208 ±  16.215  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  436.815 ±  65.580  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  446.811 ±  69.701  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  447.929 ±  69.576  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  449.149 ±  71.435  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  438.747 ±  60.579  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  392.143 ±  49.908  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5  457.577 ±  59.999  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5  316.785 ± 204.595  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5  378.371 ±  46.067  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5  392.387 ±  47.146  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5  372.072 ±  41.842  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5  363.017 ±  53.318  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  449.509 ±  78.395  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  453.663 ±  78.182  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  461.665 ±  82.776  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  461.506 ±  80.737  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  456.544 ±  87.787  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  405.259 ±  55.778  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5   85.089 ±   5.298  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5  101.293 ±   7.068  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5  113.914 ±   7.442  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5  125.261 ±   9.928  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5   59.518 ± 160.868  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5  136.764 ±  11.591  ops/us
 * <p>
 * 4 Threads
 * Benchmark                                          (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5   54.374 ±  57.820  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5   95.719 ±   2.665  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5   63.246 ±   2.812  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5  132.889 ±   7.012  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5   61.266 ±   0.622  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5  171.581 ±  14.350  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  855.106 ± 124.646  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  863.937 ± 129.551  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  792.323 ±  92.234  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  855.928 ± 112.756  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  855.597 ± 115.271  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  773.848 ±  96.067  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5  727.697 ± 134.366  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5  692.711 ±  83.724  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5  243.364 ±  46.102  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5  570.200 ±  83.661  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5  603.515 ± 120.031  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5  236.861 ± 369.828  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  853.616 ±  97.877  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  904.158 ± 161.162  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  894.610 ± 153.754  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  895.105 ± 139.893  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  889.385 ± 137.138  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  794.543 ± 103.095  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5   92.685 ±   2.863  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5  112.153 ±  40.423  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5   94.878 ± 102.083  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5  139.112 ±   8.259  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5   86.289 ± 145.943  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5  158.678 ±  18.119  ops/us
 * <p>
 * 8 Threads
 * Benchmark                                          (arrayLength)   Mode  Cnt     Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5    40.157 ±   0.759  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5    62.322 ±   1.122  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5    69.351 ±   1.026  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5    85.613 ±   1.266  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5    96.397 ±   2.086  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5   112.798 ±   2.082  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  1445.905 ± 272.959  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  1591.037 ± 202.481  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  1459.474 ± 342.354  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  1639.142 ± 201.034  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  1676.567 ± 187.149  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  1510.558 ± 157.747  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5  1042.901 ± 369.052  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5   592.385 ± 284.281  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5   834.874 ± 386.702  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5   998.729 ± 751.654  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5   955.288 ± 414.432  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5   676.560 ± 236.944  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  1342.332 ± 220.316  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  1550.924 ± 158.716  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  1519.573 ± 323.725  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  1682.518 ± 184.943  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  1698.723 ± 183.750  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  1566.287 ± 192.972  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5    42.738 ±   1.837  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5    62.620 ±   1.939  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5    73.824 ±   1.962  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5    84.264 ±   1.444  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5   102.030 ±   3.595  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5   116.463 ±   4.833  ops/us
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
    public int fastAtomicIntArrayV0Bench(ThreadState state) {
        return fastAtomicIntArrayV0.getAndIncrement(randomKey(state));
    }

    @Benchmark
    public int fastAtomicIntArrayV1Bench(ThreadState state) {
        return fastAtomicIntArrayV1.getAndIncrement(randomKey(state));
    }

    @Benchmark
    public int fastAtomicIntArrayV2Bench(ThreadState state) {
        return fastAtomicIntArrayV2.getAndIncrement(randomKey(state));
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行3遍warmup，然后执行5遍测试
        final Options opt = new OptionsBuilder()
                .include(AtomicIntArrayIncrBench.class.getSimpleName())
                .jvmArgs("-server", "-Xmx8G", "-Xms8G")
//                .addProfiler(AsyncProfiler.class, "output=flamegraph;dir=jmh.csv")
//                .addProfiler(StackProfiler.class)
                .forks(1)
                .threads(8)
                .warmupIterations(1)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
