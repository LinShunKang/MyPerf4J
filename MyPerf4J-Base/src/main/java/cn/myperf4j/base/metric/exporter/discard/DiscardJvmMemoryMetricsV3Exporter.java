package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsV3Exporter;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class DiscardJvmMemoryMetricsV3Exporter implements JvmMemoryMetricsV3Exporter {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmMemoryMetricsV3 metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }
}
