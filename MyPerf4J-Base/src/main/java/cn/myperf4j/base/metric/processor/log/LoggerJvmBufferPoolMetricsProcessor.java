package cn.myperf4j.base.metric.processor.log;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefJvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.metric.processor.AbstractJvmBufferPoolMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class LoggerJvmBufferPoolMetricsProcessor extends AbstractJvmBufferPoolMetricsProcessor {

    private final ConcurrentHashMap<Long, List<JvmBufferPoolMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private static final JvmBufferPoolMetricsFormatter METRICS_FORMATTER = new DefJvmBufferPoolMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmBufferPoolMetrics>(2));
    }

    @Override
    public void process(JvmBufferPoolMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmBufferPoolMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("LoggerJvmBufferPoolMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmBufferPoolMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("LoggerJvmBufferPoolMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }
}
