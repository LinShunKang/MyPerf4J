package MyPerf4J;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.core.*;
import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;
import cn.myperf4j.core.recorder.RoughRecorder;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class MethodMetricsTest {

    @Test
    public void test() {
        Recorders recorders = new Recorders(new AtomicReferenceArray<Recorder>(10));
        MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

        int methodId1 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("Test", "test1", "", ""));
        recorders.setRecorder(methodId1, AccurateRecorder.getInstance(0, 100000, 50));

        int methodId2 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("Test", "test1", "", ""));
        recorders.setRecorder(methodId2, RoughRecorder.getInstance(0, 100000));

        testRecorder(recorders, methodTagMaintainer, methodId1);
        testRecorder(recorders, methodTagMaintainer, methodId2);
    }

    private void testRecorder(Recorders recorders, MethodTagMaintainer methodTagMaintainer, int methodId) {
        Recorder recorder = recorders.getRecorder(methodId);
        recorders.setStartTime(System.currentTimeMillis());
        long start = System.nanoTime();
        for (long i = 0; i < 100000; ++i) {
            recorder.recordTime(start, start + (i + 1) * 1000 * 1000);
        }
        recorders.setStopTime(System.currentTimeMillis());

        MethodTag methodTag = methodTagMaintainer.getMethodTag(methodId);
        MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime());
        System.out.println(methodMetrics);
        recorder.resetRecord();

        System.out.println(methodMetrics.getTP50() == 50000);
        System.out.println(methodMetrics.getTP90() == 90000);
        System.out.println(methodMetrics.getTP95() == 95000);
        System.out.println(methodMetrics.getTP99() == 99000);
        System.out.println(methodMetrics.getTP999() == 99900);
        System.out.println(methodMetrics.getTP9999() == 99990);
        System.out.println(methodMetrics.getTP99999() == 99999);
        System.out.println(methodMetrics.getTP100() == 100000);
    }
}
