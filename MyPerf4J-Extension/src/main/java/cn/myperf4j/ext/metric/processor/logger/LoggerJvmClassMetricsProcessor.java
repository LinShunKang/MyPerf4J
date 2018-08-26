package cn.myperf4j.ext.metric.processor.logger;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.JvmClassMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmClassMetricsFormatter;
import cn.myperf4j.base.metric.processor.JvmClassMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class LoggerJvmClassMetricsProcessor implements JvmClassMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(LoggerJvmClassMetricsProcessor.class);

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
            logger.error("LoggerJvmClassMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JvmClassMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                logger.info(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                logger.error("LoggerJvmClassMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
