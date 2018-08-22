package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JVMThreadMetrics;
import cn.myperf4j.base.metric.formatter.JVMThreadMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJVMThreadMetricsFormatter;
import cn.myperf4j.base.metric.processor.JVMThreadMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJVMThreadMetricsProcessor implements JVMThreadMetricsProcessor {

    private ConcurrentHashMap<Long, List<JVMThreadMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JVMThreadMetricsFormatter metricsFormatter = new DefaultJVMThreadMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JVMThreadMetrics>(1));
    }

    @Override
    public void process(JVMThreadMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JVMThreadMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdoutJVMThreadMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JVMThreadMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                Logger.error("StdoutJVMThreadMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
