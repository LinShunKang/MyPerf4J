package cn.myperf4j.base.metric.exporter.log.standard;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.standard.StdMethodMetricsFormatter;
import cn.myperf4j.base.metric.formatter.MethodMetricsFormatter;
import cn.myperf4j.base.metric.exporter.log.AbstractLogMethodMetricsExporter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/7/11
 */
public class StdLogMethodMetricsExporter extends AbstractLogMethodMetricsExporter {

    private static final MethodMetricsFormatter METRICS_FORMATTER = new StdMethodMetricsFormatter();

    private final ConcurrentHashMap<Long, List<MethodMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<MethodMetrics>(64));
    }

    @Override
    public void process(MethodMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<MethodMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdLogMethodMetricsExporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<MethodMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("StdLogMethodMetricsExporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
