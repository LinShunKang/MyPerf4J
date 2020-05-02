package cn.myperf4j.bench;

import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.RoughRecorder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2019/10/19
 */
@BenchmarkMode({Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class RecorderBenchmark {

    private Recorder roughRecorder;

    private Recorder accurateRecorder;

    @Setup
    public void setup() {
        roughRecorder = RoughRecorder.getInstance(0, 1024);
        accurateRecorder = AccurateRecorder.getInstance(1, 1024, 64);
    }

    @Benchmark
    public void roughRecorderBench() {
        roughRecorder.recordTime(0L, 1000000000L);
    }

    @Benchmark
    public void accurateRecorderBench() {
        accurateRecorder.recordTime(0L, 1000000000L);
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行3遍warmup，然后执行5遍测试
        Options opt = new OptionsBuilder()
                .include(RecorderBenchmark.class.getSimpleName())
                .forks(2)
                .threads(8)
                .warmupIterations(3)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
