package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.formatter.JvmGcMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmGcMetricsFormatter implements JvmGcMetricsFormatter {

    @Override
    public String format(List<JvmGcMetrics> metricsList, long startMillis, long stopMillis) {
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

    private void appendLineProtocol(JvmGcMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_gc_metrics_v2")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" YoungGcCount=").append(metrics.getYoungGcCount()).append('i')
                .append(",YoungGcTime=").append(metrics.getYoungGcTime()).append('i')
                .append(",AvgYoungGcTime=").append(doubleFormat(metrics.getAvgYoungGcTime()))
                .append(",FullGcCount=").append(metrics.getFullGcCount()).append('i')
                .append(",FullGcTime=").append(metrics.getFullGcTime()).append('i')
                .append(",ZGcTime=").append(metrics.getZGcTime()).append('i')
                .append(",ZGcCount=").append(metrics.getZGcCount()).append('i')
                .append(",AvgZGcTime=").append(doubleFormat(metrics.getAvgZGcTime()))
                .append(",ZGcCyclesTime=").append(metrics.getZGcCyclesTime()).append('i')
                .append(",ZGcCyclesCount=").append(metrics.getZGcCyclesCount()).append('i')
                .append(",AvgZGcCyclesTime=").append(doubleFormat(metrics.getAvgZGcCyclesTime()))
                .append(",ZGcPausesTime=").append(metrics.getZGcPausesTime()).append('i')
                .append(",ZGcPausesCount=").append(metrics.getZGcPausesCount()).append('i')
                .append(",AvgZGcPausesTime=").append(doubleFormat(metrics.getAvgZGcPausesTime()))
                .append(' ').append(startNanos).append('\n');
    }
}
