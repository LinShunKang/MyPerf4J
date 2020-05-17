package cn.myperf4j.base.metric.processor.log.influxdb;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.formatter.JvmGCMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmGcMetricsFormatter;
import cn.myperf4j.base.metric.processor.log.AbstractLogJvmGcMetricsProcessor;

import java.util.Collections;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxLogJvmGcMetricsProcessor extends AbstractLogJvmGcMetricsProcessor {

    private static final JvmGCMetricsFormatter METRICS_FORMATTER = new InfluxJvmGcMetricsFormatter();

    @Override
    public void process(JvmGcMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(Collections.singletonList(metrics), startMillis, stopMillis));
    }

}
