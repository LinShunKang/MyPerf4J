package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmMemoryMetricsFormatter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxLogJvmMemoryMetricsExporter extends AbstractLogJvmMemoryMetricsExporter {

    private static final InfluxJvmMemoryMetricsFormatter FORMATTER = new InfluxJvmMemoryMetricsFormatter();

    @Override
    public void process(JvmMemoryMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(FORMATTER.format(singletonList(metrics), startMillis, stopMillis).toString(UTF_8));
    }
}
