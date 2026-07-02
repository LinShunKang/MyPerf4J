package cn.myperf4j.base.metric.exporter.log.standard;

import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmMemoryMetricsV3Exporter;
import cn.myperf4j.base.metric.formatter.standard.StdJvmMemoryMetricsV3Formatter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class StdLogJvmMemoryMetricsV3Exporter extends AbstractLogJvmMemoryMetricsV3Exporter {

    private static final StdJvmMemoryMetricsV3Formatter FORMATTER = new StdJvmMemoryMetricsV3Formatter();

    private final ConcurrentMap<Long, List<JvmMemoryMetricsV3>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<>(8));
    }

    @Override
    public void process(JvmMemoryMetricsV3 metrics, long processId, long startMillis, long stopMillis) {
        final List<JvmMemoryMetricsV3> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdLogJvmMemoryMetricsV3Exporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        final List<JvmMemoryMetricsV3> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("StdLogJvmMemoryMetricsV3Exporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
