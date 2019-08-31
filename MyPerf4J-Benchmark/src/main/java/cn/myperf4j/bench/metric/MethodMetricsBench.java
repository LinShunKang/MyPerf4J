package cn.myperf4j.bench.metric;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.core.MethodMetricsCalculator;
import cn.myperf4j.core.MethodTagMaintainer;
import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;
import cn.myperf4j.core.recorder.RoughRecorder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2019/08/31
 */
@BenchmarkMode({Mode.AverageTime, Mode.SampleTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class MethodMetricsBench {

    private Recorders recorders;

    private Recorder roughRecorder;

    private Recorder accurateRecorder;

    private MethodTag roughMethodTag;

    private MethodTag accurateMethodTag;

    @Setup
    public void setup() {
        recorders = new Recorders(new AtomicReferenceArray<Recorder>(10));
        MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

        roughMethodTag = MethodTag.getGeneralInstance("", "Test", "Api", "rough", "");
        int roughMethodId = methodTagMaintainer.addMethodTag(roughMethodTag);
        roughRecorder = RoughRecorder.getInstance(0, 1024);
        recorders.setRecorder(roughMethodId, roughRecorder);

        accurateMethodTag = MethodTag.getGeneralInstance("", "Test", "Api", "accurate", "");
        int accurateMethodId = methodTagMaintainer.addMethodTag(accurateMethodTag);
        accurateRecorder = AccurateRecorder.getInstance(1, 1024, 64);
        recorders.setRecorder(accurateMethodId, accurateRecorder);

        long startTime = System.currentTimeMillis();
        recorders.setStartTime(startTime);

        long startNano = System.nanoTime();
        for (long i = 0; i < 1024; ++i) {
            for (int k = 0; k < 10240; k++) {
                roughRecorder.recordTime(startNano, startNano + i * 1000 * 1000);
                accurateRecorder.recordTime(startNano, startNano + i * 1000 * 1000);
            }
        }
        recorders.setStopTime(startTime + 60 * 1000);
    }

    @Benchmark
    public MethodMetrics roughRecorder() {
        return MethodMetricsCalculator.calPerfStats(roughRecorder, roughMethodTag, recorders.getStartTime(), recorders.getStopTime());
    }

    @Benchmark
    public MethodMetrics accurateRecorder() {
        return MethodMetricsCalculator.calPerfStats(accurateRecorder, accurateMethodTag, recorders.getStartTime(), recorders.getStopTime());
    }

    public static void main(String[] args) throws RunnerException {
        // 使用一个单独进程执行测试，执行5遍warmup，然后执行5遍测试
        Options opt = new OptionsBuilder()
                .include(MethodMetricsBench.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
