package cn.myperf4j.base.metric.exporter.log.influxdb;

import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmGcMetricsV3Exporter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmGcMetricsV3Formatter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class InfluxLogJvmGcMetricsV3Exporter extends AbstractLogJvmGcMetricsV3Exporter {

    private static final InfluxJvmGcMetricsV3Formatter FORMATTER = new InfluxJvmGcMetricsV3Formatter();

    @Override
    public void process(JvmGcMetricsV3 metrics, long processId, long startMillis, long stopMillis) {
        logger.log(FORMATTER.format(singletonList(metrics), startMillis, stopMillis).toString(UTF_8));
    }
}
