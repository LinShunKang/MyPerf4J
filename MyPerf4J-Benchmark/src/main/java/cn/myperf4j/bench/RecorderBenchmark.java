package cn.myperf4j.bench;

import cn.myperf4j.core.recorder.DefaultRecorder;
import cn.myperf4j.core.recorder.Recorder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;

/**
 * Created by LinShunkang on 2019/10/19
 */
@Threads(value = 8)
@State(Scope.Thread)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
@Fork(value = 2, jvmArgs = {"-server", "-Xmx8G", "-Xms8G", "-Xmn4G"})
public class RecorderBenchmark {

    private Recorder accurateRecorder;

    @Setup
    public void setup() {
        accurateRecorder = DefaultRecorder.getInstance(1, 1024, 64);
    }

    @Benchmark
    public void accurateRecorderBench() {
        accurateRecorder.recordTime(0L, 1000000000L);
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(RecorderBenchmark.class.getSimpleName()).build()).run();
    }
}
