package cn.myperf4j.base.metric.processor.log.influxdb;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.formatter.JvmCompilationMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmCompilationMetricsFormatter;
import cn.myperf4j.base.metric.processor.log.AbstractLogJvmCompilationMetricsProcessor;

import java.util.Collections;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class InfluxLogJvmCompilationMetricsProcessor extends AbstractLogJvmCompilationMetricsProcessor {

    private static final JvmCompilationMetricsFormatter METRICS_FORMATTER = new InfluxJvmCompilationMetricsFormatter();

    @Override
    public void process(JvmCompilationMetrics metrics, long processId, long startMillis, long stopMillis) {
        logger.log(METRICS_FORMATTER.format(Collections.singletonList(metrics), startMillis, stopMillis));
    }

}
