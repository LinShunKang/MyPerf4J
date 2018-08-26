package cn.myperf4j.ext.metric.processor.logger;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.JvmThreadMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmThreadMetricsFormatter;
import cn.myperf4j.base.metric.processor.JvmThreadMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class LoggerJvmThreadMetricsProcessor implements JvmThreadMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(LoggerJvmThreadMetricsProcessor.class);

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
            logger.error("StdoutJvmThreadMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        try {
            List<JvmThreadMetrics> metricsList = metricsMap.get(processId);
            if (metricsList != null) {
                logger.info(metricsFormatter.format(metricsList, startMillis, stopMillis));
            } else {
                logger.error("StdoutJvmThreadMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
            }
        } finally {
            metricsMap.remove(processId);
        }
    }
}
