package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmClassMetricsProcessor;


/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmClassMetricsProcessor extends AbstractJvmClassMetricsProcessor {

    @Override
    public void process(JvmClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

}
