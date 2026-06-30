package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmCompilationMetricsFormatter implements BinaryMetricsFormatter<JvmCompilationMetrics> {

    @Override
    public Bytes format(List<JvmCompilationMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmCompilationMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes();
        }
    }

    private void appendLineProtocol(JvmCompilationMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_compilation_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" Time=").append(metrics.getTime()).append('i')
                .append(",TotalTime=").append(metrics.getTotalTime()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
