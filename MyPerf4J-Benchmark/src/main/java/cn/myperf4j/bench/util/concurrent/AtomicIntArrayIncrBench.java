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
 * # VM options: -Xmx8G
 * <p>
 * 1 Thread
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3  204.538 ± 213.170  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3  205.133 ± 213.462  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  205.224 ± 213.065  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  203.966 ± 190.569  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  204.170 ± 196.586  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  205.207 ± 211.588  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  186.278 ± 158.407  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  186.257 ± 162.914  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  186.355 ± 165.664  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  186.694 ± 160.409  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  184.634 ± 153.048  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  165.800 ± 119.479  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3  250.655 ± 285.653  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3  251.673 ± 289.978  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  250.943 ± 302.382  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  250.181 ± 304.693  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  252.246 ± 286.960  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  252.211 ± 290.494  ops/us
 * <p>
 * 2 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3   84.258 ±  21.532  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3   96.135 ±  35.130  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  105.585 ±  30.009  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  115.343 ±  36.660  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  119.906 ±  58.527  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  121.368 ±  46.001  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  360.199 ± 259.017  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  360.296 ± 319.708  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  357.740 ± 295.515  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  362.586 ± 316.470  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  358.302 ± 319.169  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  321.219 ± 241.128  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3   81.454 ±  27.601  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3   96.250 ±  51.752  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  109.330 ±  49.745  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  118.971 ±  62.283  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  124.547 ±  67.055  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  128.513 ±  57.851  ops/us
 * <p>
 * 4 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3   78.321 ±  15.323  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3   97.029 ±  15.993  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  120.032 ±  16.683  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  137.981 ±  29.453  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  151.101 ±  25.181  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  158.647 ±  28.549  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  709.966 ± 550.162  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  695.539 ± 628.425  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  710.790 ± 553.496  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  708.575 ± 583.708  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  690.424 ± 528.263  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  625.484 ± 495.149  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3   76.587 ±  20.020  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3   98.101 ±  25.106  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  125.099 ±  39.456  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  142.808 ±  39.517  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  153.850 ±  48.394  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  163.104 ±  73.914  ops/us
 * <p>
 * 8 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt     Score      Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3    42.478 ±    1.269  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3    58.225 ±    2.231  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3    71.755 ±    3.873  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3    81.903 ±    7.326  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3    98.648 ±    5.807  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3   110.625 ±   10.661  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  1203.547 ±  708.820  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  1257.794 ±  906.897  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  1262.673 ± 2291.898  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  1362.071 ± 1345.609  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  1358.223 ± 1115.284  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  1237.142 ±  817.137  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3    43.246 ±    2.134  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3    58.119 ±    3.655  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3    68.695 ±    4.609  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3    84.519 ±    8.442  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3   102.424 ±   11.817  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3   114.831 ±   23.700  ops/us
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
    public int fastAtomicIntArrayBench(ThreadState state) {
        return fastAtomicIntArray.getAndIncrement(randomKey(state));
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
