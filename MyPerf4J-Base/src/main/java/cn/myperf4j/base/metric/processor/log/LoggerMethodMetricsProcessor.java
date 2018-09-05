package cn.myperf4j.base.metric.processor.log;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.impl.DefaultMethodMetricsFormatter;
import cn.myperf4j.base.metric.formatter.MethodMetricsFormatter;
import cn.myperf4j.base.metric.processor.AbstractMethodMetricsProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/7/11
 */
public class LoggerMethodMetricsProcessor extends AbstractMethodMetricsProcessor {

    private ConcurrentHashMap<Long, List<MethodMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private MethodMetricsFormatter formatter = new DefaultMethodMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<MethodMetrics>(64));
    }

    @Override
    public void process(MethodMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<MethodMetrics> methodMetrics = metricsMap.get(processId);
        methodMetrics.add(metrics);
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<MethodMetrics> methodMetrics = metricsMap.get(processId);
        logger.log(formatter.format(methodMetrics, startMillis, stopMillis));
    }
}
