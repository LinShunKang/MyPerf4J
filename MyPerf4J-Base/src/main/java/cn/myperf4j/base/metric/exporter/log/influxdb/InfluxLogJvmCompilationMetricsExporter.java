package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.formatter.JvmCompilationMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmCompilationMetricsFormatter;

import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class InfluxLogJvmCompilationMetricsExporter extends AbstractLogJvmCompilationMetricsExporter {

    private static final JvmCompilationMetricsFormatter METRICS_FORMATTER = new InfluxJvmCompilationMetricsFormatter();

    @Override
    public void process(JvmCompilationMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(singletonList(metrics), startMillis, stopMillis));
    }
}
