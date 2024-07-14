package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.formatter.JvmFileDescMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmFileDescMetricsFormatter;

import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class InfluxLogJvmFileDescMetricsExporter extends AbstractLogJvmFileDescMetricsExporter {

    private static final JvmFileDescMetricsFormatter METRICS_FORMATTER = new InfluxJvmFileDescMetricsFormatter();

    @Override
    public void process(JvmFileDescriptorMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(singletonList(metrics), startMillis, stopMillis));
    }
}
