package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JVMClassMetrics;
import cn.myperf4j.base.metric.formatter.JVMClassMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJVMClassMetricsFormatter;
import cn.myperf4j.base.metric.processor.JVMClassMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJVMClassMetricsProcessor implements JVMClassMetricsProcessor {

    private ConcurrentHashMap<Long, List<JVMClassMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JVMClassMetricsFormatter metricsFormatter = new DefaultJVMClassMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JVMClassMetrics>(1));
    }

    @Override
    public void process(JVMClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JVMClassMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdoutMethodMetricProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JVMClassMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                Logger.error("StdoutMethodMetricProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
