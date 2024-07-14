package cn.myperf4j.base.metric.exporter.log.standard;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.formatter.JvmCompilationMetricsFormatter;
import cn.myperf4j.base.metric.formatter.standard.StdJvmCompilationMetricsFormatter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class StdLogJvmCompilationMetricsExporter extends AbstractLogJvmCompilationMetricsExporter {

    private static final JvmCompilationMetricsFormatter METRICS_FORMATTER = new StdJvmCompilationMetricsFormatter();

    private final ConcurrentMap<Long, List<JvmCompilationMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<>(1));
    }

    @Override
    public void process(JvmCompilationMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmCompilationMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdLogJvmCompilationMetricsExporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmCompilationMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("StdLogJvmCompilationMetricsExporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
