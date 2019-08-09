package MyPerf4J;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.core.MethodMetricsCalculator;
import cn.myperf4j.core.MethodTagMaintainer;
import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;
import cn.myperf4j.core.recorder.RoughRecorder;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2019/06/22
 */
public class MethodMetricsBenchmark {

    public static void main(String[] args) {
        Recorders recorders = new Recorders(new AtomicReferenceArray<Recorder>(10));
        MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

        int methodId1 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("", "Test", "Api", "m1", ""));
//        recorders.setRecorder(methodId1, AccurateRecorder.getInstance(0, 9000, 50));
        recorders.setRecorder(methodId1, RoughRecorder.getInstance(0, 10000));

        Recorder recorder = recorders.getRecorder(methodId1);
        recorders.setStartTime(System.currentTimeMillis());
        long start = System.nanoTime();
        for (long i = 0; i < 1000; ++i) {
            recorder.recordTime(start, start + i * 1000 * 1000);
        }
        recorders.setStopTime(System.currentTimeMillis());

        MethodTag methodTag = methodTagMaintainer.getMethodTag(methodId1);

        long tmp = 0L;
        start = System.nanoTime();
        for (int i = 0; i < 1000000; ++i) {
            MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime());
            tmp += methodMetrics.getRPS();
        }
        System.out.println("tmp=" + tmp + ", totalCost=" + (System.nanoTime() - start) / 1000_000 + "ms");
    }

}
