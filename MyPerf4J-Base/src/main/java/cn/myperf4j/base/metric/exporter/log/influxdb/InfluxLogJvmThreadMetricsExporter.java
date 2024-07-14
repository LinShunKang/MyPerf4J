package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmThreadMetricsExporter;
import cn.myperf4j.base.metric.formatter.JvmThreadMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmThreadMetricsFormatter;

import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxLogJvmThreadMetricsExporter extends AbstractLogJvmThreadMetricsExporter {

    private static final JvmThreadMetricsFormatter METRICS_FORMATTER = new InfluxJvmThreadMetricsFormatter();

    @Override
    public void process(JvmThreadMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(singletonList(metrics), startMillis, stopMillis));
    }
}
