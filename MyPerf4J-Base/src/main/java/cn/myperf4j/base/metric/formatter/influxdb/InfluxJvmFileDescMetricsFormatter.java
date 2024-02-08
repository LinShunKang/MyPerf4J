package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.JvmFileDescMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmFileDescMetricsFormatter implements JvmFileDescMetricsFormatter {

    @Override
    public String format(List<JvmFileDescriptorMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            final long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0, size = metricsList.size(); i < size; ++i) {
                appendLineProtocol(metricsList.get(i), startNanos, sb);
            }
            return sb.substring(0, Math.max(0, sb.length() - 1));
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmFileDescriptorMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_file_descriptor_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" OpenCount=").append(metrics.getOpenCount()).append('i')
                .append(",OpenPercent=").append(metrics.getOpenPercent())
                .append(' ').append(startNanos).append('\n');
    }
}
