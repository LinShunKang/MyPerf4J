package cn.myperf4j.base.metric.processor.log.influxdb;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.JvmThreadMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmThreadMetricsFormatter;
import cn.myperf4j.base.metric.processor.log.AbstractLogJvmThreadMetricsProcessor;

import java.util.Collections;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxLogJvmThreadMetricsProcessor extends AbstractLogJvmThreadMetricsProcessor {

    private static final JvmThreadMetricsFormatter METRICS_FORMATTER = new InfluxJvmThreadMetricsFormatter();

    @Override
    public void process(JvmThreadMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(Collections.singletonList(metrics), startMillis, stopMillis));
    }

}
