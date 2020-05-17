package cn.myperf4j.base.metric.processor.log.influxdb;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.MethodMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxMethodMetricsFormatter;
import cn.myperf4j.base.metric.processor.log.AbstractLogMethodMetricsProcessor;

import java.util.Collections;

/**
 * Created by LinShunkang on 2018/7/9
 */
public class InfluxLogMethodMetricsProcessor extends AbstractLogMethodMetricsProcessor {

    private static final MethodMetricsFormatter METRICS_FORMATTER = new InfluxMethodMetricsFormatter();

    @Override
    public void process(MethodMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(Collections.singletonList(metrics), startMillis, stopMillis));
    }

}
