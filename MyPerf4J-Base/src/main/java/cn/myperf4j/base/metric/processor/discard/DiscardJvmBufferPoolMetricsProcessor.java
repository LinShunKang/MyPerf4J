package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmBufferPoolMetricsProcessor;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class DiscardJvmBufferPoolMetricsProcessor extends AbstractJvmBufferPoolMetricsProcessor {

    @Override
    public void process(JvmBufferPoolMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

}
