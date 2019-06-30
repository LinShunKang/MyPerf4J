package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmGcMetricsProcessor;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmGcMetricsProcessor extends AbstractJvmGcMetricsProcessor {

    @Override
    public void process(JvmGcMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

}
