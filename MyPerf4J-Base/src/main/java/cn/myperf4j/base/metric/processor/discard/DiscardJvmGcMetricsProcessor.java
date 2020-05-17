package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.processor.JvmGcMetricsProcessor;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmGcMetricsProcessor implements JvmGcMetricsProcessor {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmGcMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

}
