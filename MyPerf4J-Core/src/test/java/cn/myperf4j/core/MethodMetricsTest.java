package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.core.recorder.AccurateRecorder;
import cn.myperf4j.core.recorder.Recorder;
import cn.myperf4j.core.recorder.Recorders;
import cn.myperf4j.core.recorder.RoughRecorder;
import org.junit.Assert;
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

        int methodId1 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("", "Test", "Api", "m1", ""));
        recorders.setRecorder(methodId1, AccurateRecorder.getInstance(0, 9000, 50));
        testNormalRecord(recorders, methodTagMaintainer, methodId1);

        int methodId2 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("", "Test", "Api", "m2", ""));
        recorders.setRecorder(methodId2, RoughRecorder.getInstance(0, 10000));
        testNormalRecord(recorders, methodTagMaintainer, methodId2);

        int methodId3 = methodTagMaintainer.addMethodTag(MethodTag.getGeneralInstance("", "Test", "Api", "m3", ""));
        recorders.setRecorder(methodId3, RoughRecorder.getInstance(0, 10000));
        testZeroRecor(recorders, methodTagMaintainer, methodId3);
    }

    private void testNormalRecord(Recorders recorders, MethodTagMaintainer methodTagMaintainer, int methodId) {
        Recorder recorder = recorders.getRecorder(methodId);
        recorders.setStartTime(System.currentTimeMillis());
        long start = System.nanoTime();
        for (long i = 0; i < 10000; ++i) {
            recorder.recordTime(start, start + i * 1000 * 1000);
        }
        recorders.setStopTime(System.currentTimeMillis());

        MethodTag methodTag = methodTagMaintainer.getMethodTag(methodId);
        MethodMetrics methodMetrics = MethodMetricsCalculator.calPerfStats(recorder, methodTag, recorders.getStartTime(), recorders.getStopTime());
        System.out.println(methodMetrics);
        recorder.resetRecord();

        Assert.assertEquals(methodMetrics.getMinTime(), 0);
        assert methodMetrics.getAvgTime() == 4999.5D;
        Assert.assertEquals(methodMetrics.getTP50(), 4999);
        Assert.assertEquals(methodMetrics.getTP90(), 8999);
        Assert.assertEquals(methodMetrics.getTP95(), 9499);
        Assert.assertEquals(methodMetrics.getTP99(), 9899);
        Assert.assertEquals(methodMetrics.getTP999(), 9989);
        Assert.assertEquals(methodMetrics.getTP9999(), 9998);
        Assert.assertEquals(methodMetrics.getTP100(), 9999);
        Assert.assertEquals(methodMetrics.getTP100(), methodMetrics.getMaxTime());
    }

    private void testZeroRecor(Recorders recorders, MethodTagMaintainer methodTagMaintainer, int methodId) {
        Recorder recorder = recorders.getRecorder(methodId);
        recorders.setStartTime(System.currentTimeMillis());
        recorders.setStopTime(System.currentTimeMillis() + 1000);

        MethodTag methodTag = methodTagMaintainer.getMethodTag(methodId);
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
