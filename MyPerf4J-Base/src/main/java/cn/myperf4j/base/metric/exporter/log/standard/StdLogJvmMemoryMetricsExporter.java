package cn.myperf4j.base.metric.exporter.log.standard;

import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JvmMemoryMetricsFormatter;
import cn.myperf4j.base.metric.formatter.standard.StdJvmMemoryMetricsFormatter;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmMemoryMetricsExporter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class StdLogJvmMemoryMetricsExporter extends AbstractLogJvmMemoryMetricsExporter {

    private static final JvmMemoryMetricsFormatter METRICS_FORMATTER = new StdJvmMemoryMetricsFormatter();

    private final ConcurrentHashMap<Long, List<JvmMemoryMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmMemoryMetrics>(2));
    }

    @Override
    public void process(JvmMemoryMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmMemoryMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdLogJvmMemoryMetricsExporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmMemoryMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("StdLogJvmMemoryMetricsExporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
