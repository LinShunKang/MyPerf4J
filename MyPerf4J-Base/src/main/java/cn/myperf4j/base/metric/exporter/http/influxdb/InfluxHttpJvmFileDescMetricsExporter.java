package cn.myperf4j.base.metric.exporter.http.influxdb;

import cn.myperf4j.base.influxdb.InfluxDbClient;
import cn.myperf4j.base.influxdb.InfluxDbClientFactory;
import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.JvmFileDescMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmFileDescMetricsFormatter;
import cn.myperf4j.base.metric.exporter.JvmFileDescMetricsExporter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2020/05/23
 */
public class InfluxHttpJvmFileDescMetricsExporter implements JvmFileDescMetricsExporter {

    private static final JvmFileDescMetricsFormatter METRICS_FORMATTER = new InfluxJvmFileDescMetricsFormatter();

    private static final InfluxDbClient CLIENT = InfluxDbClientFactory.getClient();

    private final ConcurrentMap<Long, List<JvmFileDescriptorMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmFileDescriptorMetrics>(1));
    }

    @Override
    public void process(JvmFileDescriptorMetrics metrics, long processId, long startMillis, long stopMillis) {
        final List<JvmFileDescriptorMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("InfluxHttpJvmFileDescMetricsExporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        final List<JvmFileDescriptorMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            CLIENT.writeMetricsAsync(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("InfluxHttpJvmFileDescMetricsExporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
