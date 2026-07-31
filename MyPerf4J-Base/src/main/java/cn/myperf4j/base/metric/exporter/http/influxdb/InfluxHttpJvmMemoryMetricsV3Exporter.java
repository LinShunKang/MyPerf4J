package cn.myperf4j.base.metric.exporter.http.influxdb;

import cn.myperf4j.base.influxdb.InfluxDbClient;
import cn.myperf4j.base.influxdb.InfluxDbClientFactory;
import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsV3Exporter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmMemoryMetricsV3Formatter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class InfluxHttpJvmMemoryMetricsV3Exporter implements JvmMemoryMetricsV3Exporter {

    private static final InfluxJvmMemoryMetricsV3Formatter FORMATTER = new InfluxJvmMemoryMetricsV3Formatter();

    private static final InfluxDbClient CLIENT = InfluxDbClientFactory.getClient();

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
            Logger.error("InfluxHttpJvmMemoryMetricsV3Exporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        final List<JvmMemoryMetricsV3> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            CLIENT.writeMetricsAsync(FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("InfluxHttpJvmMemoryMetricsV3Exporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }
}
