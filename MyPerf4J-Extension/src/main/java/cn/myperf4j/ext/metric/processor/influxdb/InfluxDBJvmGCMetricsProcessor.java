package cn.myperf4j.ext.metric.processor.influxdb;

import cn.myperf4j.base.metric.JvmGCMetrics;
import cn.myperf4j.base.metric.processor.impl.AbstractJvmGCMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxDBJvmGCMetricsProcessor extends AbstractJvmGCMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(InfluxDBJvmGCMetricsProcessor.class);

    private ThreadLocal<StringBuilder> sbThreadLocal = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public void process(JvmGCMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = sbThreadLocal.get();
        try {
            logger.info(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmGCMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_gc")
                .append(",GCName=").append(metrics.getGcName().replace(' ', '-')).append("")
                .append(" CollectCount=").append(metrics.getCollectCount()).append("i")
                .append(",CollectTime=").append(metrics.getCollectTime()).append("i")
                .append(" ").append(startNanos);
        return sb.toString();
    }
}
