package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.JvmThreadMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmThreadMetricsFormatter;
import cn.myperf4j.base.metric.processor.JvmThreadMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJvmThreadMetricsProcessor implements JvmThreadMetricsProcessor {

    private ConcurrentHashMap<Long, List<JvmThreadMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JvmThreadMetricsFormatter metricsFormatter = new DefaultJvmThreadMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmThreadMetrics>(1));
    }

    @Override
    public void process(JvmThreadMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmThreadMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdoutJvmThreadMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JvmThreadMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                Logger.error("StdoutJvmThreadMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
