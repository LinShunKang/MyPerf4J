package cn.myperf4j.base.metric.processor.log;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.formatter.JvmCompilationMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmCompilationMetricsFormatter;
import cn.myperf4j.base.metric.processor.AbstractJvmCompilationProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class LoggerJvmCompilationMetricsProcessor extends AbstractJvmCompilationProcessor {

    private ConcurrentHashMap<Long, List<JvmCompilationMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JvmCompilationMetricsFormatter metricsFormatter = new DefaultJvmCompilationMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmCompilationMetrics>(1));
    }

    @Override
    public void process(JvmCompilationMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmCompilationMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("LoggerJvmCompilationMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmCompilationMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(metricsFormatter.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("LoggerJvmCompilationMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }
}
