package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmClassMetricsFormatter implements BinaryMetricsFormatter<JvmClassMetrics> {

    @Override
    public Bytes format(List<JvmClassMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmClassMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmClassMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_class_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" Total=").append(metrics.getTotal()).append('i')
                .append(",Loaded=").append(metrics.getLoaded()).append('i')
                .append(",Unloaded=").append(metrics.getUnloaded()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
