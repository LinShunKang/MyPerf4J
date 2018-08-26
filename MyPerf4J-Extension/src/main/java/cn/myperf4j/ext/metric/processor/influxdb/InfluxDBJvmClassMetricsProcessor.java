package cn.myperf4j.ext.metric.processor.influxdb;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.processor.impl.AbstractJvmClassMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LinShunkang on 2018/8/23
 */
public class InfluxDBJvmClassMetricsProcessor extends AbstractJvmClassMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(InfluxDBJvmClassMetricsProcessor.class);

    private ThreadLocal<StringBuilder> sbThreadLocal = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public void process(JvmClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = sbThreadLocal.get();
        try {
            logger.info(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmClassMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_class")
                .append(" Total=").append(metrics.getTotal()).append("i")
                .append(",Loaded=").append(metrics.getLoaded()).append("i")
                .append(",Unloaded=").append(metrics.getUnloaded()).append("i")
                .append(" ").append(startNanos);
        return sb.toString();
    }

}
