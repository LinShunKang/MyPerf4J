package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmMemoryMetricsV3Exporter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmMemoryMetricsV3Formatter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class InfluxLogJvmMemoryMetricsV3Exporter extends AbstractLogJvmMemoryMetricsV3Exporter {

    private static final InfluxJvmMemoryMetricsV3Formatter FORMATTER = new InfluxJvmMemoryMetricsV3Formatter();

    @Override
    public void process(JvmMemoryMetricsV3 metrics, long processId, long startMillis, long stopMillis) {
        logger.log(FORMATTER.format(singletonList(metrics), startMillis, stopMillis).toString(UTF_8));
    }
}
