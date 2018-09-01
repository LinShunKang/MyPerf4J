package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JvmThreadMetrics;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmThreadMetricsProcessor extends AbstractJvmThreadMetricsProcessor {

    @Override
    public void process(JvmThreadMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

}
