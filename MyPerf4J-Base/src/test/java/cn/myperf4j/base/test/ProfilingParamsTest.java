package cn.myperf4j.base.test;

import cn.myperf4j.base.config.ProfilingParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class ProfilingParamsTest {

    @Test
    public void test() {
        ProfilingParams params = ProfilingParams.of(1000, 10);
        Assertions.assertEquals(1000, params.mostTimeThreshold());
        Assertions.assertNotEquals(-1000, params.mostTimeThreshold());
        Assertions.assertEquals(10, params.outThresholdCount());
        Assertions.assertNotEquals(-10, params.outThresholdCount());
    }
}
