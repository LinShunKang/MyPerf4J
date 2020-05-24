package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.JvmClassMetricsFormatter;
import cn.myperf4j.base.util.IpUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmClassMetricsFormatter implements JvmClassMetricsFormatter {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public String format(List<JvmClassMetrics> metricsList, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0; i < metricsList.size(); ++i) {
                JvmClassMetrics metrics = metricsList.get(i);
                appendLineProtocol(metrics, startNanos, sb);
                sb.append('\n');
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmClassMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_class_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(",host=").append(IpUtils.getLocalhostName())
                .append(" Total=").append(metrics.getTotal()).append('i')
                .append(",Loaded=").append(metrics.getLoaded()).append('i')
                .append(",Unloaded=").append(metrics.getUnloaded()).append('i')
                .append(' ').append(startNanos);
    }
}
