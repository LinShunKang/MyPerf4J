package cn.myperf4j.base.metric.processor.log.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmClassMetricsProcessor;

/**
 * Created by LinShunkang on 2018/8/23
 */
public class InfluxLogJvmClassMetricsProcessor extends AbstractJvmClassMetricsProcessor {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public void process(JvmClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmClassMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_class_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(" Total=").append(metrics.getTotal()).append('i')
                .append(",Loaded=").append(metrics.getLoaded()).append('i')
                .append(",Unloaded=").append(metrics.getUnloaded()).append('i')
                .append(' ').append(startNanos);
        return sb.toString();
    }

}
