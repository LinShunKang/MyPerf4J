package cn.myperf4j.base.metric.exporter.http.influxdb;

import cn.myperf4j.base.influxdb.InfluxDbClient;
import cn.myperf4j.base.influxdb.InfluxDbClientFactory;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.MethodMetricsFormatter;
import cn.myperf4j.base.metric.formatter.influxdb.InfluxMethodMetricsFormatter;
import cn.myperf4j.base.metric.exporter.MethodMetricsExporter;
import cn.myperf4j.base.util.ListUtils;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2020/05/17
 */
public class InfluxHttpMethodMetricsExporter implements MethodMetricsExporter {

    private static final int BATCH_SIZE = 64;

    private static final MethodMetricsFormatter METRICS_FORMATTER = new InfluxMethodMetricsFormatter();

    private static final InfluxDbClient CLIENT = InfluxDbClientFactory.getClient();

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
            Logger.error("InfluxHttpMethodMetricsExporter.process(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<MethodMetrics> metricsList = metricsMap.remove(processId);
        if (ListUtils.isEmpty(metricsList)) {
            Logger.warn("InfluxHttpMethodMetricsExporter.afterProcess(" + processId + ", " + startMillis + ", "
                    + stopMillis + "): metricsList is empty!!!");
            return;
        }

        if (metricsList.size() <= BATCH_SIZE) {
            CLIENT.writeMetricsAsync(METRICS_FORMATTER.format(metricsList, startMillis, stopMillis));
        } else {
            List<List<MethodMetrics>> partition = ListUtils.partition(metricsList, BATCH_SIZE);
            for (int i = 0; i < partition.size(); i++) {
                CLIENT.writeMetricsAsync(METRICS_FORMATTER.format(partition.get(i), startMillis, stopMillis));
            }
        }
    }
}
