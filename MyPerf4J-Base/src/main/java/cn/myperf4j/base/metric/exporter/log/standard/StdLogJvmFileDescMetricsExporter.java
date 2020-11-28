package cn.myperf4j.base.metric.exporter.log.standard;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.JvmFileDescMetricsFormatter;
import cn.myperf4j.base.metric.formatter.standard.StdJvmFileDescMetricsFormatter;
import cn.myperf4j.base.metric.exporter.log.AbstractLogJvmFileDescMetricsExporter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class StdLogJvmFileDescMetricsExporter extends AbstractLogJvmFileDescMetricsExporter {

    private static final JvmFileDescMetricsFormatter METRICS_FORMATTER = new StdJvmFileDescMetricsFormatter();

    private final ConcurrentMap<Long, List<JvmFileDescriptorMetrics>> metricsMap = new ConcurrentHashMap<>(8);

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
            Logger.error("StdLogJvmFileDescMetricsExporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmFileDescriptorMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("StdLogJvmFileDescMetricsExporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
