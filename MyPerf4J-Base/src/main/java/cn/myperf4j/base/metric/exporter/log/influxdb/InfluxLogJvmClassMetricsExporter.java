package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmClassMetricsExporter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmClassMetricsFormatter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2018/8/23
 */
public class InfluxLogJvmClassMetricsExporter extends AbstractLogJvmClassMetricsExporter {

    private static final InfluxJvmClassMetricsFormatter FORMATTER = new InfluxJvmClassMetricsFormatter();

    @Override
    public void process(JvmClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(FORMATTER.format(singletonList(metrics), startMillis, stopMillis).toString(UTF_8));
    }
}
