package cn.myperf4j.base.test;

import cn.myperf4j.base.config.ProfilingParams;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class ProfilingParamsTest {

    @Test
    public void test() {
        ProfilingParams params = ProfilingParams.of(1000, 10);
        Assert.assertEquals(1000, params.mostTimeThreshold());
        Assert.assertNotEquals(-1000, params.mostTimeThreshold());
        Assert.assertEquals(10, params.outThresholdCount());
        Assert.assertNotEquals(-10, params.outThresholdCount());
    }
}
