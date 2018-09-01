package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JvmGCMetrics;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmGCMetricsProcessor extends AbstractJvmGCMetricsProcessor {

    @Override
    public void process(JvmGCMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

}
