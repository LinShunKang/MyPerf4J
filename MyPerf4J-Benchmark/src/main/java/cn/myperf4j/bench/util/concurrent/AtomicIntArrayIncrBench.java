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
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3  190.437 ± 132.065  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3  191.245 ± 111.900  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  191.221 ± 114.326  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  191.431 ± 111.622  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  191.471 ± 110.544  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  192.009 ± 100.033  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  186.986 ± 111.040  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  188.462 ± 111.896  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  183.502 ± 105.141  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  186.667 ± 105.049  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  186.346 ± 108.030  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  170.527 ±  84.522  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3  193.011 ± 329.966  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3  220.014 ±  94.773  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  220.194 ±  90.262  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  215.342 ± 126.146  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  220.227 ±  97.075  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  220.276 ±  95.353  ops/us
 * <p>
 * 2 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score    Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3   76.549 ± 34.650  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3   93.923 ± 31.487  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  105.202 ± 30.493  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  113.662 ± 53.577  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  120.145 ± 37.483  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  123.516 ± 52.225  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  119.167 ± 37.159  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  124.286 ± 62.733  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  124.415 ± 56.582  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  125.994 ± 72.339  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  131.197 ± 64.282  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  147.811 ± 90.207  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3   75.596 ± 18.272  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3   92.353 ± 22.518  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  106.393 ± 31.484  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  114.595 ± 38.787  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  123.433 ± 31.139  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  125.878 ± 67.423  ops/us
 * <p>
 * 4 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3   78.390 ±   9.304  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3  101.101 ±  44.572  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3  124.250 ±  35.649  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3  142.097 ±  47.808  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3  160.689 ±  51.602  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  168.207 ±  58.159  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3  158.331 ±  56.688  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3  168.003 ±  46.156  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  168.367 ±  59.330  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3  171.716 ±  58.443  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3  177.694 ±  83.164  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3  186.247 ± 100.620  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3   81.241 ±   1.350  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3  104.759 ±  26.261  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3  128.202 ±  23.951  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3  148.082 ±  31.806  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  162.809 ±  46.514  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  168.649 ±  38.378  ops/us
 * <p>
 * 8 Threads
 * Benchmark                                        (arrayLength)   Mode  Cnt    Score     Error   Units
 * AtomicIntArrayIncrBench.atomicIntArrayBench                128  thrpt    3   37.642 ±   2.936  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                256  thrpt    3   53.077 ±   3.436  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench                512  thrpt    3   65.975 ±   5.751  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               1024  thrpt    3   70.065 ±  20.141  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               2048  thrpt    3   93.715 ±   7.701  ops/us
 * AtomicIntArrayIncrBench.atomicIntArrayBench               4096  thrpt    3  102.116 ±   5.721  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            128  thrpt    3   91.885 ±   8.714  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            256  thrpt    3   98.997 ±   7.615  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench            512  thrpt    3  112.083 ±   4.265  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           1024  thrpt    3   87.896 ± 213.932  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           2048  thrpt    3   78.286 ±   1.545  ops/us
 * AtomicIntArrayIncrBench.fastAtomicIntArrayBench           4096  thrpt    3   76.200 ±  46.791  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   128  thrpt    3   42.561 ±   3.610  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   256  thrpt    3   53.397 ±   0.504  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                   512  thrpt    3   68.594 ±   0.251  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  1024  thrpt    3   88.966 ±   8.321  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  2048  thrpt    3  102.150 ±   9.087  ops/us
 * AtomicIntArrayIncrBench.jdkIntArrayBench                  4096  thrpt    3  109.655 ±   6.429  ops/us
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
        return fastAtomicIntArray.incrementAndGet(randomKey(state));
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行3遍warmup，然后执行5遍测试
        final Options opt = new OptionsBuilder()
                .include(AtomicIntArrayIncrBench.class.getSimpleName())
                .jvmArgs("-Xmx8G")
                .forks(1)
                .threads(8)
                .warmupIterations(1)
                .measurementIterations(3)
                .build();
        new Runner(opt).run();
    }
}
