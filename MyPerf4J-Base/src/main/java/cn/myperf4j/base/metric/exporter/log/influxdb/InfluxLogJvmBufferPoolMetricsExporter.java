package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmBufferPoolMetricsFormatter;

import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxLogJvmBufferPoolMetricsExporter extends AbstractLogJvmBufferPoolMetricsExporter {

    private static final JvmBufferPoolMetricsFormatter METRICS_FORMATTER = new InfluxJvmBufferPoolMetricsFormatter();

    @Override
    public void process(JvmBufferPoolMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(singletonList(metrics), startMillis, stopMillis));
    }
}
