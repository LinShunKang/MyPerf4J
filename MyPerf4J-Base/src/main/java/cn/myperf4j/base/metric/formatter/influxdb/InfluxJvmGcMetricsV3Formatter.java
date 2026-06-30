package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class InfluxJvmGcMetricsV3Formatter implements BinaryMetricsFormatter<JvmGcMetricsV3> {

    @Override
    public Bytes format(List<JvmGcMetricsV3> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmGcMetricsV3 metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes();
        }
    }

    private void appendLineProtocol(JvmGcMetricsV3 metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_gc_metrics_v3")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(",GcName=").append(processTagOrField(metrics.getGcName()))
                .append(" GcCount=").append(metrics.getGcCount()).append('i')
                .append(",GcTime=").append(metrics.getGcTime()).append('i')
                .append(",AvgGcTime=").append(doubleFormat(metrics.getAvgGcTime()))
                .append(' ').append(startNanos).append('\n');
    }
}
