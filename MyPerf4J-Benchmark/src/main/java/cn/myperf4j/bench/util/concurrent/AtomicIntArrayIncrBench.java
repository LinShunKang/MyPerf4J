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
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5  204.646 ± 36.410  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5  205.346 ± 33.301  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5  205.150 ± 33.213  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5  204.817 ± 33.356  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5  205.090 ± 34.475  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5  203.779 ± 32.107  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  182.436 ± 20.789  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  183.048 ± 21.409  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  185.157 ± 26.331  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  185.186 ± 26.198  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  182.732 ± 25.591  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  162.754 ± 20.133  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5  191.923 ± 30.645  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5  168.735 ± 22.526  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5  164.412 ± 17.918  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5  161.640 ± 16.890  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5  160.958 ± 16.974  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5  151.258 ± 13.584  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  187.902 ± 31.185  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  189.473 ± 30.556  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  189.135 ± 29.571  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  189.357 ± 29.363  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  187.079 ± 29.781  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  166.382 ± 23.050  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5  247.749 ± 42.438  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5  249.237 ± 43.293  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5  249.514 ± 44.434  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5  248.821 ± 44.715  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5  250.678 ± 45.574  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5  250.803 ± 45.411  ops/us
 * <p>
 * 2 Threads
 * Benchmark                                          (arrayLength)   Mode  Cnt    Score    Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5   75.824 ±  3.797  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5   87.552 ±  3.593  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5   97.051 ±  6.771  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5  104.871 ±  5.408  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5  108.493 ±  3.582  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5  110.446 ±  7.233  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  365.927 ± 52.673  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  357.430 ± 49.769  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  366.697 ± 53.196  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  366.388 ± 56.583  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  361.183 ± 49.365  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  322.775 ± 42.694  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5  313.244 ± 60.441  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5  275.493 ± 33.582  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5  301.782 ± 34.441  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5  313.195 ± 40.211  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5  261.106 ± 25.100  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5   24.762 ±  2.991  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  364.999 ± 56.296  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  368.793 ± 59.576  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  366.744 ± 54.992  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  368.957 ± 55.933  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  369.002 ± 57.285  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  328.044 ± 45.443  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5   76.805 ±  5.256  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5   88.935 ±  6.625  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5  106.184 ±  9.084  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5  109.573 ±  9.133  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5  120.173 ±  8.551  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5  119.168 ±  6.979  ops/us
 * <p>
 * 4 Threads
 * Benchmark                                          (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5   69.926 ±  17.043  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5   82.322 ±   8.324  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5  113.001 ±  42.321  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5  112.171 ±  19.794  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5  128.023 ±  23.363  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5   99.784 ±  93.414  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  688.304 ±  97.560  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  716.468 ± 102.154  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  714.537 ± 105.611  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  707.909 ±  95.475  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  702.139 ±  97.300  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  632.563 ±  83.832  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5  546.615 ± 125.247  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5  334.654 ± 172.414  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5  296.561 ± 159.232  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5  416.103 ±  92.313  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5  494.126 ± 208.695  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5  395.044 ± 116.557  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  730.609 ± 121.549  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  700.901 ± 114.545  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  727.853 ± 109.187  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  714.174 ± 119.995  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  720.873 ± 114.503  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  644.352 ±  85.666  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5   86.485 ±  10.724  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5  116.692 ±  11.104  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5  136.367 ±  16.624  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5  155.555 ±  20.007  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5  169.518 ±  29.030  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5  177.614 ±  20.152  ops/us
 * <p>
 * 8 Threads
 * Benchmark                                          (arrayLength)   Mode  Cnt     Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  128  thrpt    5    40.572 ±   0.234  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  256  thrpt    5    53.960 ±   0.347  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                  512  thrpt    5    67.439 ±   1.876  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 1024  thrpt    5    85.864 ±   0.957  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 2048  thrpt    5    96.607 ±   1.619  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                 4096  thrpt    5   103.494 ±   1.815  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            128  thrpt    5  1259.197 ± 176.973  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            256  thrpt    5  1287.515 ± 207.980  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench            512  thrpt    5  1343.038 ± 185.288  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           1024  thrpt    5  1345.424 ± 232.892  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           2048  thrpt    5  1377.668 ± 206.870  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV0Bench           4096  thrpt    5  1236.345 ± 169.407  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            128  thrpt    5   816.727 ± 281.604  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            256  thrpt    5   512.686 ± 140.842  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench            512  thrpt    5   442.936 ± 166.011  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           1024  thrpt    5   541.468 ± 173.209  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           2048  thrpt    5   559.234 ± 187.450  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV1Bench           4096  thrpt    5   624.181 ± 225.299  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            128  thrpt    5  1204.092 ± 160.496  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            256  thrpt    5  1280.131 ± 200.682  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench            512  thrpt    5  1365.857 ± 213.385  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           1024  thrpt    5  1437.223 ± 222.389  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           2048  thrpt    5  1401.216 ± 210.506  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayV2Bench           4096  thrpt    5  1264.029 ± 161.443  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     128  thrpt    5    40.968 ±   0.886  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     256  thrpt    5    56.477 ±   0.671  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                     512  thrpt    5    71.878 ±   1.043  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    1024  thrpt    5    90.873 ±   1.778  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    2048  thrpt    5   102.605 ±   2.363  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                    4096  thrpt    5   118.961 ±   3.969  ops/us
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
        return atomicIntArray.getAndIncrement(randomKey(state));
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
                .threads(1)
                .warmupIterations(1)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
