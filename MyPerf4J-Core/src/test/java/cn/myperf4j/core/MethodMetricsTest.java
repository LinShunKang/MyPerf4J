package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.core.recorder.DefaultRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReferenceArray;

import static cn.myperf4j.core.MethodMetricsCalculator.calMetrics;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class MethodMetricsTest {

    private final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private final Recorders recorders = new Recorders(new AtomicReferenceArray<Recorder>(10));

    private Recorder recorder;

    @BeforeEach
    public void init() {
        final MethodTag methodTag = MethodTag.getGeneralInstance("", "Test", "Api", "m1", "");
        final int methodTagId = methodTagMaintainer.addMethodTag(methodTag);
        recorder = DefaultRecorder.getInstance(methodTagId, 1000, 50);
    }

    @Test
    public void testUniformDistribution() {
        final Recorder recorder = DefaultRecorder.getInstance(1, 128, 512);
        final long start = System.nanoTime();
        for (long i = 1; i <= 10000; ++i) {
            recorder.recordTime(start, start + i * 1000 * 1000);
        }

        final long startMillis = System.currentTimeMillis();
        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics methodMetrics = calMetrics(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assertions.assertEquals(1, methodMetrics.getMinTime());
        assert methodMetrics.getAvgTime() == 5000.5D;
        Assertions.assertEquals(5000, methodMetrics.getTP50());
        Assertions.assertEquals(9000, methodMetrics.getTP90());
        Assertions.assertEquals(9500, methodMetrics.getTP95());
        Assertions.assertEquals(9900, methodMetrics.getTP99());
        Assertions.assertEquals(9990, methodMetrics.getTP999());
        Assertions.assertEquals(9999, methodMetrics.getTP9999());
        Assertions.assertEquals(10000, methodMetrics.getTP100());
        Assertions.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    @Test
    public void testExtremelyUnevenDistribution() {
        recordRecords(recorder, 1L, 10000);

        final long startMillis = System.currentTimeMillis();
        final MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics methodMetrics = calMetrics(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assertions.assertEquals(1, methodMetrics.getMinTime());
        assert methodMetrics.getAvgTime() == 1D;
        Assertions.assertEquals(1, methodMetrics.getTP50());
        Assertions.assertEquals(1, methodMetrics.getTP90());
        Assertions.assertEquals(1, methodMetrics.getTP95());
        Assertions.assertEquals(1, methodMetrics.getTP99());
        Assertions.assertEquals(1, methodMetrics.getTP999());
        Assertions.assertEquals(1, methodMetrics.getTP9999());
        Assertions.assertEquals(1, methodMetrics.getTP100());
        Assertions.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    private void recordRecords(Recorder recorder, long elapsedMills, int times) {
        final long start = System.nanoTime();
        for (long i = 0; i < times; ++i) {
            recorder.recordTime(start, start + elapsedMills * 1000 * 1000L);
        }
    }

    @Test
    public void testExtremelyUnevenDistribution2() {
        recordRecords(recorder, 1L, 4000);
        recordRecords(recorder, 2L, 3000);
        recordRecords(recorder, 3L, 2000);
        recordRecords(recorder, 4L, 500);
        recordRecords(recorder, 5L, 300);
        recordRecords(recorder, 6L, 200);

        final long startMillis = System.currentTimeMillis();
        final MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        final MethodMetrics methodMetrics = calMetrics(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assertions.assertEquals(1, methodMetrics.getMinTime());
        assert methodMetrics.getAvgTime() == 2.07D;
        Assertions.assertEquals(2, methodMetrics.getTP50());
        Assertions.assertEquals(3, methodMetrics.getTP90());
        Assertions.assertEquals(4, methodMetrics.getTP95());
        Assertions.assertEquals(6, methodMetrics.getTP99());
        Assertions.assertEquals(6, methodMetrics.getTP999());
        Assertions.assertEquals(6, methodMetrics.getTP9999());
        Assertions.assertEquals(6, methodMetrics.getTP100());
        Assertions.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    @Test
    public void testExtremelyUnevenDistribution3() {
        recordRecords(recorder, 1L, 4000);
        recordRecords(recorder, 2L, 5600);
        recordRecords(recorder, 3L, 300);
        recordRecords(recorder, 4L, 90);
        recordRecords(recorder, 5L, 9);
        recordRecords(recorder, 6L, 1);

        final long startMillis = System.currentTimeMillis();
        final MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        final MethodMetrics methodMetrics = calMetrics(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assertions.assertEquals(1, methodMetrics.getMinTime());
        assert methodMetrics.getAvgTime() == 1.6511D;
        Assertions.assertEquals(2, methodMetrics.getTP50());
        Assertions.assertEquals(2, methodMetrics.getTP90());
        Assertions.assertEquals(2, methodMetrics.getTP95());
        Assertions.assertEquals(3, methodMetrics.getTP99());
        Assertions.assertEquals(4, methodMetrics.getTP999());
        Assertions.assertEquals(5, methodMetrics.getTP9999());
        Assertions.assertEquals(6, methodMetrics.getTP100());
        Assertions.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    @Test
    public void testZeroRecord() {
        recorders.setStartTime(System.currentTimeMillis());
        recorders.setStopTime(System.currentTimeMillis() + 1000);

        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics metrics = calMetrics(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime());
        System.out.println(metrics);
        recorder.resetRecord();

        Assertions.assertEquals(-1, metrics.getMinTime());
        assert metrics.getAvgTime() == -1D;
        Assertions.assertEquals(-1, metrics.getTP50());
        Assertions.assertEquals(-1, metrics.getTP90());
        Assertions.assertEquals(-1, metrics.getTP95());
        Assertions.assertEquals(-1, metrics.getTP99());
        Assertions.assertEquals(-1, metrics.getTP999());
        Assertions.assertEquals(-1, metrics.getTP9999());
        Assertions.assertEquals(-1, metrics.getTP100());
        Assertions.assertEquals(metrics.getTP100(), metrics.getMaxTime());
    }
}
