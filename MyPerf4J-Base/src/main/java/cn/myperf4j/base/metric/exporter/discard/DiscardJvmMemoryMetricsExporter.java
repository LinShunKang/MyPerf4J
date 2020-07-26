package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsExporter;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmMemoryMetricsExporter implements JvmMemoryMetricsExporter {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmMemoryMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }
}
