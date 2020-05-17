package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.processor.JvmThreadMetricsProcessor;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmThreadMetricsProcessor  implements JvmThreadMetricsProcessor {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmThreadMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

}
