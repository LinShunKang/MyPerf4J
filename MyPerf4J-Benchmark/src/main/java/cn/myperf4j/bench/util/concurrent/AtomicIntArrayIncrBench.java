package cn.myperf4j.bench.util.concurrent;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static org.openjdk.jmh.runner.options.TimeValue.seconds;

/**
 * Created by LinShunkang on 2024/02/15
 * # JMH version: 1.37
 * # VM version: JDK 1.8.0_362, OpenJDK 64-Bit Server VM, 25.362-b09
 * # VM options: -server -Xmx8G -Xms8G
 * <p>
 * 1 Thread
 * Benchmark                                     (arrayLength)   Mode  Cnt    Score    Error   Units
 * AtomicIntArrayIncrBench.fastAtomicIntArray              128  thrpt    5  233.451 ± 40.020  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              256  thrpt    5  235.920 ± 32.742  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              512  thrpt    5  234.977 ± 41.371  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             1024  thrpt    5  234.513 ± 41.209  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             2048  thrpt    5  231.613 ± 31.159  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             4096  thrpt    5  205.691 ± 25.522  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     128  thrpt    5  246.662 ± 38.844  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     256  thrpt    5  248.282 ± 39.260  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     512  thrpt    5  254.004 ± 47.375  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    1024  thrpt    5  248.441 ± 40.966  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    2048  thrpt    5  248.673 ± 39.572  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    4096  thrpt    5  248.049 ± 37.587  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            128  thrpt    5  250.715 ± 47.570  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            256  thrpt    5  250.936 ± 46.758  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            512  thrpt    5  250.016 ± 46.566  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           1024  thrpt    5  251.621 ± 46.997  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           2048  thrpt    5  252.200 ± 52.065  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           4096  thrpt    5  251.776 ± 48.118  ops/us
 * <p>
 * 2 Threads
 * Benchmark                                     (arrayLength)   Mode  Cnt    Score    Error   Units
 * AtomicIntArrayIncrBench.fastAtomicIntArray              128  thrpt    5  437.348 ± 65.116  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              256  thrpt    5  443.685 ± 63.557  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              512  thrpt    5  446.729 ± 71.568  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             1024  thrpt    5  456.179 ± 73.681  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             2048  thrpt    5  447.481 ± 67.087  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             4096  thrpt    5  400.676 ± 53.358  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     128  thrpt    5   69.070 ±  3.143  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     256  thrpt    5   80.851 ±  5.077  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     512  thrpt    5   91.194 ±  5.700  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    1024  thrpt    5   97.803 ±  8.188  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    2048  thrpt    5  103.176 ± 10.449  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    4096  thrpt    5  107.803 ±  4.547  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            128  thrpt    5   72.472 ±  5.283  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            256  thrpt    5   78.976 ±  9.040  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            512  thrpt    5   93.021 ±  3.946  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           1024  thrpt    5   98.806 ±  5.753  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           2048  thrpt    5  101.144 ±  7.873  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           4096  thrpt    5  106.136 ±  8.455  ops/us
 * <p>
 * 4 Threads
 * Benchmark                                     (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.fastAtomicIntArray              128  thrpt    5  828.578 ± 119.910  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              256  thrpt    5  884.688 ± 144.104  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              512  thrpt    5  887.291 ± 130.602  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             1024  thrpt    5  867.644 ± 138.855  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             2048  thrpt    5  873.532 ± 137.715  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             4096  thrpt    5  778.624 ± 106.777  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     128  thrpt    5   64.652 ±  12.038  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     256  thrpt    5   83.257 ±   5.183  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     512  thrpt    5  102.695 ±  19.789  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    1024  thrpt    5  123.331 ±   8.650  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    2048  thrpt    5   86.782 ±  96.405  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    4096  thrpt    5  143.247 ±  46.603  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            128  thrpt    5   50.642 ±  65.267  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            256  thrpt    5   91.261 ±   5.223  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            512  thrpt    5   95.044 ±  97.763  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           1024  thrpt    5  122.452 ±  20.414  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           2048  thrpt    5  142.241 ±  29.947  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           4096  thrpt    5  150.885 ±  19.335  ops/us
 * <p>
 * 8 Threads
 * Benchmark                                     (arrayLength)   Mode  Cnt     Score     Error   Units
 * AtomicIntArrayIncrBench.fastAtomicIntArray              128  thrpt    5  1735.897 ± 318.564  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              256  thrpt    5  1476.587 ± 152.711  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray              512  thrpt    5  1632.896 ± 184.194  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             1024  thrpt    5  1631.265 ± 202.180  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             2048  thrpt    5  1678.818 ± 208.511  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArray             4096  thrpt    5  1520.658 ± 174.527  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     128  thrpt    5    41.658 ±   0.567  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     256  thrpt    5    59.514 ±   1.563  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                     512  thrpt    5    68.666 ±   0.918  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    1024  thrpt    5    78.980 ±   1.696  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    2048  thrpt    5   102.853 ±   2.751  ops/us
 * AtomicIntArrayIncrBench.jdkIntArray                    4096  thrpt    5   117.731 ±  11.280  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            128  thrpt    5    40.959 ±   0.376  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            256  thrpt    5    54.912 ±   0.530  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray            512  thrpt    5    70.729 ±   1.753  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           1024  thrpt    5    90.039 ±   6.282  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           2048  thrpt    5    94.170 ±   2.007  ops/us
 * AtomicIntArrayIncrBench.simpleAtomicIntArray           4096  thrpt    5   115.178 ±   2.930  ops/us
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
    public int jdkIntArray(ThreadState state) {
        return jdkIntArray.incrementAndGet(randomKey(state));
    }

    private int randomKey(ThreadState state) {
        return state.next() & (arrayLength - 1);
    }

    @Benchmark
    public int simpleAtomicIntArray(ThreadState state) {
        return simpleAtomicIntArray.getAndIncrement(randomKey(state));
    }

    @Benchmark
    public int fastAtomicIntArray(ThreadState state) {
        return fastAtomicIntArray.getAndIncrement(randomKey(state));
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
                .warmupTime(seconds(3))
                .warmupIterations(1)
                .measurementTime(seconds(5))
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
