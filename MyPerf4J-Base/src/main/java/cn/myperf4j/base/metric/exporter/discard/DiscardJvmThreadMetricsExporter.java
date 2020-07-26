package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.exporter.JvmThreadMetricsExporter;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmThreadMetricsExporter implements JvmThreadMetricsExporter {

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
