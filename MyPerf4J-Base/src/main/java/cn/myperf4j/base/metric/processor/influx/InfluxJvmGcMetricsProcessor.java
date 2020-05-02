package cn.myperf4j.base.metric.processor.influx;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmGcMetricsProcessor;
import cn.myperf4j.base.util.NumFormatUtils;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxJvmGcMetricsProcessor extends AbstractJvmGcMetricsProcessor {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(256);
        }
    };

    @Override
    public void process(JvmGcMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmGcMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_gc_metrics_v2")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(" YoungGcCount=").append(metrics.getYoungGcCount()).append('i')
                .append(",YoungGcTime=").append(metrics.getYoungGcTime()).append('i')
                .append(",AvgYoungGcTime=").append(NumFormatUtils.doubleFormat(metrics.getAvgYoungGcTime()))
                .append(",FullGcCount=").append(metrics.getFullGcCount()).append('i')
                .append(",FullGcTime=").append(metrics.getFullGcTime()).append('i')
                .append(",ZGcTime=").append(metrics.getZGcTime()).append('i')
                .append(",ZGcCount=").append(metrics.getZGcCount()).append('i')
                .append(",AvgZGcTime=").append(NumFormatUtils.doubleFormat(metrics.getAvgZGcTime()))
                .append(' ').append(startNanos);
        return sb.toString();
    }
}
