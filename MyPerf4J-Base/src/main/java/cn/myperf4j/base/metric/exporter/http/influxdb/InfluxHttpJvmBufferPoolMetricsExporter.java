package cn.myperf4j.base.metric.exporter.http.influxdb;

import cn.myperf4j.base.influxdb.InfluxDbClient;
import cn.myperf4j.base.influxdb.InfluxDbClientFactory;
import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.metric.exporter.JvmBufferPoolMetricsExporter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2020/05/23
 */
public class InfluxHttpJvmBufferPoolMetricsExporter implements JvmBufferPoolMetricsExporter {

    private static final JvmBufferPoolMetricsFormatter METRICS_FORMATTER = new InfluxJvmBufferPoolMetricsFormatter();

    private static final InfluxDbClient CLIENT = InfluxDbClientFactory.getClient();

    private final ConcurrentMap<Long, List<JvmBufferPoolMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmBufferPoolMetrics>(2));
    }

    @Override
    public void process(JvmBufferPoolMetrics metrics, long processId, long startMillis, long stopMillis) {
        final List<JvmBufferPoolMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("InfluxHttpJvmBufferPoolMetricsExporter.process(" + processId + ", " + startMillis
                    + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        final List<JvmBufferPoolMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            CLIENT.writeMetricsAsync(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("InfluxHttpJvmBufferPoolMetricsExporter.afterProcess(" + processId + ", " + startMillis
                    + ", " + stopMillis + "): metricsList is null!!!");
        }
    }
}
