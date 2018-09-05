package cn.myperf4j.base.metric.processor.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmGCMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmGCMetricsProcessor;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxDBJvmGCMetricsProcessor extends AbstractJvmGCMetricsProcessor {

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
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmGCMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_gc_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(",GCName=").append(metrics.getGcName().replace(' ', '-'))
                .append(" CollectCount=").append(metrics.getCollectCount()).append("i")
                .append(",CollectTime=").append(metrics.getCollectTime()).append("i")
                .append(" ").append(startNanos);
        return sb.toString();
    }
}
