package cn.myperf4j.bench;

import cn.myperf4j.base.util.MyAtomicIntArray;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2020/11/24
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class AtomicIntArrayBench {

    private AtomicIntegerArray jdkArray;

    private MyAtomicIntArray myArray;

    @Setup
    public void setup() {
        jdkArray = new AtomicIntegerArray(1024);
        myArray = new MyAtomicIntArray(1024);
    }

    @Benchmark
    public int jdkArrayBench() {
        return jdkArray.getAndAdd(1, 1);
    }

    @Benchmark
    public int myArrayBench() {
        return myArray.getAndAdd(1, 1);
    }

    @Benchmark
    public int jdkArrayResetBench() {
        final AtomicIntegerArray jdkArray = this.jdkArray;
        for (int i = 0, length = jdkArray.length(); i < length; ++i) {
            jdkArray.set(i, 0);
        }
        return jdkArray.length();
    }

    @Benchmark
    public int myArrayResetBench() {
        final MyAtomicIntArray myArray = this.myArray;
        myArray.reset();
        return myArray.length();
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行3遍warmup，然后执行5遍测试
        Options opt = new OptionsBuilder()
                .include(AtomicIntArrayBench.class.getSimpleName())
                .forks(2)
                .threads(8)
                .warmupIterations(3)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
