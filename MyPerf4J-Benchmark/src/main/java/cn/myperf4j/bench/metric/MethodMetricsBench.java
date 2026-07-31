package cn.myperf4j.bench.metric;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.core.MethodMetricsCalculator;
import cn.myperf4j.core.MethodTagMaintainer;
import cn.myperf4j.core.recorder.DefaultRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;
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

import java.util.concurrent.atomic.AtomicReferenceArray;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.Mode.Throughput;

/**
 * Created by LinShunkang on 2019/08/31
 */
@Threads(value = 8)
@State(Scope.Thread)
@BenchmarkMode(Throughput)
@OutputTimeUnit(MICROSECONDS)
@Warmup(iterations = 3, time = 10, timeUnit = SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = SECONDS)
@Fork(value = 2, jvmArgs = {"-server", "-Xmx8G", "-Xms8G", "-Xmn4G"})
public class MethodMetricsBench {

    private static final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private Recorders recorders;

    private Recorder accurateRecorder;

    private MethodTag accurateMethodTag;

    @Setup
    public void setup() {
        recorders = new Recorders(new AtomicReferenceArray<>(10));
        accurateMethodTag = MethodTag.getGeneralInstance("", "Test", "Api", "accurate", "");
        accurateRecorder = DefaultRecorder.getInstance(1, 256, 64);
        recorders.setRecorder(methodTagMaintainer.addMethodTag(accurateMethodTag), accurateRecorder);

        final long startTime = System.currentTimeMillis();
        final long startNano = System.nanoTime();
        recorders.setStartTime(startTime);
        for (long i = 0; i < 1024; ++i) {
            for (int k = 0; k < 10240; k++) {
                accurateRecorder.recordTime(startNano, startNano + i * 1000 * 1000);
            }
        }
        recorders.setStopTime(startTime + 60 * 1000);
    }

    @Benchmark
    public MethodMetrics accurateRecorder() {
        return MethodMetricsCalculator.calMetrics(accurateRecorder,
                accurateMethodTag,
                recorders.getStartTime(),
                recorders.getStopTime());
    }

    public static void main(String[] args) throws RunnerException {
        new Runner(new OptionsBuilder().include(MethodMetricsBench.class.getSimpleName()).build()).run();
    }
}
