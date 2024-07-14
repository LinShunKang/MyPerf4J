package cn.myperf4j.base.metric.exporter.log.standard;

import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmGcMetricsV3Exporter;
import cn.myperf4j.base.metric.formatter.JvmGcMetricsV3Formatter;
import cn.myperf4j.base.metric.formatter.standard.StdJvmGcMetricsV3Formatter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class StdLogJvmGcMetricsV3Exporter extends AbstractLogJvmGcMetricsV3Exporter {

    private static final JvmGcMetricsV3Formatter METRICS_FORMATTER = new StdJvmGcMetricsV3Formatter();

    private final ConcurrentMap<Long, List<JvmGcMetricsV3>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<>(1));
    }

    @Override
    public void process(JvmGcMetricsV3 metrics, long processId, long startMillis, long stopMillis) {
        final List<JvmGcMetricsV3> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdLogJvmGcMetricsV3Exporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        final List<JvmGcMetricsV3> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("StdLogJvmGcMetricsV3Exporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
