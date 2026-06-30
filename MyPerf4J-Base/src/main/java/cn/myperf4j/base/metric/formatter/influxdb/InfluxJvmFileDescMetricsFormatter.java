package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmFileDescMetricsFormatter implements BinaryMetricsFormatter<JvmFileDescriptorMetrics> {

    @Override
    public Bytes format(List<JvmFileDescriptorMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmFileDescriptorMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes();
        }
    }

    private void appendLineProtocol(JvmFileDescriptorMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_file_descriptor_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" OpenCount=").append(metrics.getOpenCount()).append('i')
                .append(",OpenPercent=").append(metrics.getOpenPercent())
                .append(' ').append(startNanos).append('\n');
    }
}
