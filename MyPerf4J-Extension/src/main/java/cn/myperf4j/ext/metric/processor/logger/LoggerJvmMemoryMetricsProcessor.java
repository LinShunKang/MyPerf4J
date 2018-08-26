package cn.myperf4j.ext.metric.processor.logger;

import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JvmMemoryMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmMemoryMetricsFormatter;
import cn.myperf4j.base.metric.processor.JvmMemoryMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class LoggerJvmMemoryMetricsProcessor implements JvmMemoryMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(LoggerJvmMemoryMetricsProcessor.class);

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
            logger.error("LoggerJvmMemoryMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JvmMemoryMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                logger.info(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                logger.error("LoggerJvmMemoryMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
