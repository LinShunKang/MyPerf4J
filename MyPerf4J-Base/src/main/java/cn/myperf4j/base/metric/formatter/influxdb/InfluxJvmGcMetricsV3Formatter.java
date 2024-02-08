package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.formatter.JvmGcMetricsV3Formatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class InfluxJvmGcMetricsV3Formatter implements JvmGcMetricsV3Formatter {

    @Override
    public String format(List<JvmGcMetricsV3> metricsList, long startMillis, long stopMillis) {
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

    private void appendLineProtocol(JvmGcMetricsV3 metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_gc_metrics_v3")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(",GcName=").append(processTagOrField(metrics.getGcName()))
                .append(" GcCount=").append(metrics.getGcCount()).append('i')
                .append(",GcTime=").append(metrics.getGcTime()).append('i')
                .append(",AvgGcTime=").append(doubleFormat(metrics.getAvgGcTime()))
                .append(' ').append(startNanos).append('\n');
    }
}
