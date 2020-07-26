package cn.myperf4j.base.metric.exporter.http.influxdb;

import cn.myperf4j.base.influxdb.InfluxDbClient;
import cn.myperf4j.base.influxdb.InfluxDbClientFactory;
import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.JvmClassMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxJvmClassMetricsFormatter;
import cn.myperf4j.base.metric.exporter.JvmClassMetricsExporter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2020/05/23
 */
public class InfluxHttpJvmClassMetricsExporter implements JvmClassMetricsExporter {

    private static final JvmClassMetricsFormatter METRICS_FORMATTER = new InfluxJvmClassMetricsFormatter();

    private static final InfluxDbClient CLIENT = InfluxDbClientFactory.getClient();

    private final ConcurrentHashMap<Long, List<JvmClassMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmClassMetrics>(1));
    }

    @Override
    public void process(JvmClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmClassMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("InfluxHttpJvmClassMetricsExporter.process(" + processId + ", " + startMillis
                    + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmClassMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            CLIENT.writeMetricsAsync(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("InfluxHttpJvmClassMetricsExporter.afterProcess(" + processId + ", " + startMillis
                    + ", " + stopMillis + "): metricsList is null!!!");
        }
    }
}
