package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.core.recorder.DefaultRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;

import java.util.concurrent.atomic.AtomicReferenceArray;

import static cn.myperf4j.core.MethodMetricsCalculator.calMetrics;

/**
 * Created by LinShunkang on 2019/06/22
 */
public final class MethodMetricsBenchmark {

    private MethodMetricsBenchmark() {
        //empty
    }

    public static void main(String[] args) {
        final Recorders recorders = new Recorders(new AtomicReferenceArray<>(10));
        final MethodTagMaintainer tagMaintainer = MethodTagMaintainer.getInstance();
        final int mid = tagMaintainer.addMethodTag(MethodTag.getGeneralInstance("", "Test", "Api", "m1", ""));
        recorders.setRecorder(mid, DefaultRecorder.getInstance(0, 9000, 50));

        final Recorder recorder = recorders.getRecorder(mid);
        final long start = System.nanoTime();
        recorders.setStartTime(System.currentTimeMillis());
        for (long i = 0; i < 1000; ++i) {
            recorder.recordTime(start, start + i * 1000 * 1000);
        }
        recorders.setStopTime(System.currentTimeMillis());

        long tmp = 0L;
        final long start1 = System.nanoTime();
        final MethodTag methodTag = tagMaintainer.getMethodTag(mid);
        for (int i = 0; i < 1_000_000; ++i) {
            tmp += calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime()).getRPS();
        }
        System.out.println("tmp=" + tmp + ", totalCost=" + (System.nanoTime() - start1) / 1_000_000L + "ms");
    }
}
