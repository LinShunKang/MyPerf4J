package cn.myperf4j.base.metric.processor.impl;

import cn.myperf4j.base.metric.JvmGCMetrics;
import cn.myperf4j.base.metric.formatter.JvmGCMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmGCMetricsFormatter;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJvmGCMetricsProcessor extends AbstractJvmGCMetricsProcessor {

    private ConcurrentHashMap<Long, List<JvmGCMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JvmGCMetricsFormatter metricsFormatter = new DefaultJvmGCMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmGCMetrics>(1));
    }

    @Override
    public void process(JvmGCMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmGCMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("StdoutJvmGCMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JvmGCMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                Logger.error("StdoutJvmGCMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
