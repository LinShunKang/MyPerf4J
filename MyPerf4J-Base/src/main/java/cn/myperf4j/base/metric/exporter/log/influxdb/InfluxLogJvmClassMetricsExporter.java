package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.JvmClassMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmClassMetricsFormatter;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmClassMetricsExporter;

import java.util.Collections;

/**
 * Created by LinShunkang on 2018/8/23
 */
public class InfluxLogJvmClassMetricsExporter extends AbstractLogJvmClassMetricsExporter {

    private static final JvmClassMetricsFormatter METRICS_FORMATTER = new InfluxJvmClassMetricsFormatter();

    @Override
    public void process(JvmClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(Collections.singletonList(metrics), startMillis, stopMillis));
    }
}
