package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmGcMetricsFormatter implements BinaryMetricsFormatter<JvmGcMetrics> {

    @Override
    public Bytes format(List<JvmGcMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmGcMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmGcMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_gc_metrics_v2")
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
