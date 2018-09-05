package cn.myperf4j.base.metric.processor.stdout;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.JvmClassMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmClassMetricsFormatter;
import cn.myperf4j.base.metric.processor.AbstractJvmClassMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdoutJvmClassMetricsProcessor extends AbstractJvmClassMetricsProcessor {

    private ConcurrentHashMap<Long, List<JvmClassMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JvmClassMetricsFormatter metricsFormatter = new DefaultJvmClassMetricsFormatter();

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
            Logger.error("StdoutJvmClassMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JvmClassMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                System.out.println(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                Logger.error("StdoutJvmClassMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
