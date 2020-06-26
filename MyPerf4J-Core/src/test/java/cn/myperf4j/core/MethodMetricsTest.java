package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class MethodMetricsTest {

    private final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private final Recorders recorders = new Recorders(new AtomicReferenceArray<Recorder>(10));

    private Recorder recorder;

    @Before
    public void init() {
        MethodTag methodTag = MethodTag.getGeneralInstance("", "Test", "Api", "m1", "");
        int methodTagId = methodTagMaintainer.addMethodTag(methodTag);
        recorder = AccurateRecorder.getInstance(methodTagId, 1000, 50);
    }

    @Test
    public void testUniformDistribution() {
        Recorder recorder = AccurateRecorder.getInstance(1, 1024, 512);
        long start = System.nanoTime();
        for (long i = 1; i <= 10000; ++i) {
            recorder.recordTime(start, start + i * 1000 * 1000);
        }

        long startMillis = System.currentTimeMillis();
        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assert.assertEquals(methodMetrics.getMinTime(), 1);
        assert methodMetrics.getAvgTime() == 5000.5D;
        Assert.assertEquals(methodMetrics.getTP50(), 5000);
        Assert.assertEquals(methodMetrics.getTP90(), 9000);
        Assert.assertEquals(methodMetrics.getTP95(), 9500);
        Assert.assertEquals(methodMetrics.getTP99(), 9900);
        Assert.assertEquals(methodMetrics.getTP999(), 9990);
        Assert.assertEquals(methodMetrics.getTP9999(), 9999);
        Assert.assertEquals(methodMetrics.getTP100(), 10000);
        Assert.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    @Test
    public void testExtremelyUnevenDistribution() {
        recordRecords(recorder, 1L, 10000);

        long startMillis = System.currentTimeMillis();
        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assert.assertEquals(methodMetrics.getMinTime(), 1);
        assert methodMetrics.getAvgTime() == 1D;
        Assert.assertEquals(methodMetrics.getTP50(), 1);
        Assert.assertEquals(methodMetrics.getTP90(), 1);
        Assert.assertEquals(methodMetrics.getTP95(), 1);
        Assert.assertEquals(methodMetrics.getTP99(), 1);
        Assert.assertEquals(methodMetrics.getTP999(), 1);
        Assert.assertEquals(methodMetrics.getTP9999(), 1);
        Assert.assertEquals(methodMetrics.getTP100(), 1);
        Assert.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    private void recordRecords(Recorder recorder, long elapsedMills, int times) {
        long start = System.nanoTime();
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

        long startMillis = System.currentTimeMillis();
        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assert.assertEquals(methodMetrics.getMinTime(), 1);
        assert methodMetrics.getAvgTime() == 2.07D;
        Assert.assertEquals(methodMetrics.getTP50(), 2);
        Assert.assertEquals(methodMetrics.getTP90(), 3);
        Assert.assertEquals(methodMetrics.getTP95(), 4);
        Assert.assertEquals(methodMetrics.getTP99(), 6);
        Assert.assertEquals(methodMetrics.getTP999(), 6);
        Assert.assertEquals(methodMetrics.getTP9999(), 6);
        Assert.assertEquals(methodMetrics.getTP100(), 6);
        Assert.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    @Test
    public void testExtremelyUnevenDistribution3() {
        recordRecords(recorder, 1L, 4000);
        recordRecords(recorder, 2L, 5600);
        recordRecords(recorder, 3L, 300);
        recordRecords(recorder, 4L, 90);
        recordRecords(recorder, 5L, 9);
        recordRecords(recorder, 6L, 1);

        long startMillis = System.currentTimeMillis();
        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, startMillis, startMillis + 1000);
        System.out.println(methodMetrics);

        Assert.assertEquals(methodMetrics.getMinTime(), 1);
        assert methodMetrics.getAvgTime() == 1.6511D;
        Assert.assertEquals(methodMetrics.getTP50(), 2);
        Assert.assertEquals(methodMetrics.getTP90(), 2);
        Assert.assertEquals(methodMetrics.getTP95(), 2);
        Assert.assertEquals(methodMetrics.getTP99(), 3);
        Assert.assertEquals(methodMetrics.getTP999(), 4);
        Assert.assertEquals(methodMetrics.getTP9999(), 5);
        Assert.assertEquals(methodMetrics.getTP100(), 6);
        Assert.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    @Test
    public void testZeroRecord() {
        recorders.setStartTime(System.currentTimeMillis());
        recorders.setStopTime(System.currentTimeMillis() + 1000);

        MethodTag methodTag = methodTagMaintainer.getMethodTag(recorder.getMethodTagId());
        MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime());
        System.out.println(methodMetrics);
        recorder.resetRecord();

        Assert.assertEquals(methodMetrics.getMinTime(), -1);
        assert methodMetrics.getAvgTime() == -1D;
        Assert.assertEquals(methodMetrics.getTP50(), -1);
        Assert.assertEquals(methodMetrics.getTP90(), -1);
        Assert.assertEquals(methodMetrics.getTP95(), -1);
        Assert.assertEquals(methodMetrics.getTP99(), -1);
        Assert.assertEquals(methodMetrics.getTP999(), -1);
        Assert.assertEquals(methodMetrics.getTP9999(), -1);
        Assert.assertEquals(methodMetrics.getTP100(), -1);
        Assert.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

}
