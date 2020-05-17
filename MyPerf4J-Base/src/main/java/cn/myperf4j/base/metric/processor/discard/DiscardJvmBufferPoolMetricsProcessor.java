package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.processor.JvmBufferPoolMetricsProcessor;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class DiscardJvmBufferPoolMetricsProcessor implements JvmBufferPoolMetricsProcessor {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmBufferPoolMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

}
