package cn.myperf4j.base.metric.exporter.http.influxdb;

import cn.myperf4j.base.influxdb.InfluxDbClient;
import cn.myperf4j.base.influxdb.InfluxDbClientFactory;
import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsV3Exporter;
import cn.myperf4j.base.metric.formatter.JvmGcMetricsV3Formatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmGcMetricsV3Formatter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class InfluxHttpJvmGcMetricsV3Exporter implements JvmGcMetricsV3Exporter {

    private static final JvmGcMetricsV3Formatter METRICS_FORMATTER = new InfluxJvmGcMetricsV3Formatter();

    private static final InfluxDbClient CLIENT = InfluxDbClientFactory.getClient();

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
            Logger.error("InfluxHttpJvmGcMetricsV3Exporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        final List<JvmGcMetricsV3> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            CLIENT.writeMetricsAsync(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("InfluxHttpJvmGcMetricsV3Exporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
