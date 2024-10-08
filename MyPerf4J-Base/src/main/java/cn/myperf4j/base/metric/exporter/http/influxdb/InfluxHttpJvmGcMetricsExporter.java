package cn.myperf4j.base.metric.exporter.http.influxdb;

import cn.myperf4j.base.influxdb.InfluxDbClient;
import cn.myperf4j.base.influxdb.InfluxDbClientFactory;
import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsExporter;
import cn.myperf4j.base.metric.formatter.JvmGcMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmGcMetricsFormatter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2020/05/23
 */
public class InfluxHttpJvmGcMetricsExporter implements JvmGcMetricsExporter {

    private static final JvmGcMetricsFormatter METRICS_FORMATTER = new InfluxJvmGcMetricsFormatter();

    private static final InfluxDbClient CLIENT = InfluxDbClientFactory.getClient();

    private final ConcurrentMap<Long, List<JvmGcMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<>(1));
    }

    @Override
    public void process(JvmGcMetrics metrics, long processId, long startMillis, long stopMillis) {
        final List<JvmGcMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("InfluxHttpJvmGcMetricsExporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        final List<JvmGcMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            CLIENT.writeMetricsAsync(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("InfluxHttpJvmGcMetricsExporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
