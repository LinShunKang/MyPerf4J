package cn.myperf4j.base.metric.processor.log;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.JvmFileDescMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefJvmFileDescMetricsFormatter;
import cn.myperf4j.base.metric.processor.AbstractJvmFileDescProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class LoggerJvmFileDescMetricsProcessor extends AbstractJvmFileDescProcessor {

    private final ConcurrentHashMap<Long, List<JvmFileDescriptorMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private static final JvmFileDescMetricsFormatter METRICS_FORMATTER = new DefJvmFileDescMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmFileDescriptorMetrics>(1));
    }

    @Override
    public void process(JvmFileDescriptorMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmFileDescriptorMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("LoggerJvmFileDescMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmFileDescriptorMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("LoggerJvmFileDescMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }
}
