package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsExporter;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmGcMetricsExporter implements JvmGcMetricsExporter {

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
