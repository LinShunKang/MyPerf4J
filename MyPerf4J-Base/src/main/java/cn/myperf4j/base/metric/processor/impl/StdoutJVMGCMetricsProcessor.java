package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JVMGCMetrics;
import cn.myperf4j.base.metric.formatter.JVMGCMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJVMGCMetricsFormatter;
import cn.myperf4j.base.metric.processor.JVMGCMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJVMGCMetricsProcessor implements JVMGCMetricsProcessor {

    private ConcurrentHashMap<Long, List<JVMGCMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JVMGCMetricsFormatter metricsFormatter = new DefaultJVMGCMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JVMGCMetrics>(1));
    }

    @Override
    public void process(JVMGCMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JVMGCMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdoutJVMGCMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JVMGCMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                Logger.error("StdoutJVMGCMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
