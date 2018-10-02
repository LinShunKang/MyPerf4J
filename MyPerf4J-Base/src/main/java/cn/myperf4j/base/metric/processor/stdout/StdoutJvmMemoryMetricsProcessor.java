package cn.myperf4j.base.metric.processor.stdout;

import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JvmMemoryMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmMemoryMetricsFormatter;
import cn.myperf4j.base.metric.processor.AbstractJvmMemoryMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJvmMemoryMetricsProcessor extends AbstractJvmMemoryMetricsProcessor {

    private ConcurrentHashMap<Long, List<JvmMemoryMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JvmMemoryMetricsFormatter metricsFormatter = new DefaultJvmMemoryMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmMemoryMetrics>(2));
    }

    @Override
    public void process(JvmMemoryMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmMemoryMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdoutJvmMemoryMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmMemoryMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("StdoutJvmMemoryMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }
}
