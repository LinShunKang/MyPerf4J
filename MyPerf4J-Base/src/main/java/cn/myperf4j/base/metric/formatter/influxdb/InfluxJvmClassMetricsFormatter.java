package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.JvmClassMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmClassMetricsFormatter implements JvmClassMetricsFormatter {

    @Override
    public String format(List<JvmClassMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            final long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0; i < metricsList.size(); ++i) {
                appendLineProtocol(metricsList.get(i), startNanos, sb);
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmClassMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_class_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" Total=").append(metrics.getTotal()).append('i')
                .append(",Loaded=").append(metrics.getLoaded()).append('i')
                .append(",Unloaded=").append(metrics.getUnloaded()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
