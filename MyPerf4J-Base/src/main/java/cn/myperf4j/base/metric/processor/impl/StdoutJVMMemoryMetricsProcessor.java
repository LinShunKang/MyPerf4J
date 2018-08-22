package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JVMMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JVMMemoryMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJVMMemoryMetricsFormatter;
import cn.myperf4j.base.metric.processor.JVMMemoryMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJVMMemoryMetricsProcessor implements JVMMemoryMetricsProcessor {

    private ConcurrentHashMap<Long, List<JVMMemoryMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JVMMemoryMetricsFormatter metricsFormatter = new DefaultJVMMemoryMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JVMMemoryMetrics>(2));
    }

    @Override
    public void process(JVMMemoryMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JVMMemoryMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdoutJVMMemoryMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JVMMemoryMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                Logger.error("StdoutJVMMemoryMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
