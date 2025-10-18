package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsV3Exporter;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class DiscardJvmGcMetricsV3Exporter implements JvmGcMetricsV3Exporter {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmGcMetricsV3 metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }
}
