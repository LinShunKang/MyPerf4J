package cn.myperf4j.base.metric.processor.log.influxdb;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.metric.processor.log.AbstractLogJvmBufferPoolMetricsProcessor;

import java.util.Collections;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxLogJvmBufferPoolMetricsProcessor extends AbstractLogJvmBufferPoolMetricsProcessor {

    private static final JvmBufferPoolMetricsFormatter METRICS_FORMATTER = new InfluxJvmBufferPoolMetricsFormatter();

    @Override
    public void process(JvmBufferPoolMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(Collections.singletonList(metrics), startMillis, stopMillis));
    }

}
