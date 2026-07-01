package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class InfluxJvmMemoryMetricsV3Formatter implements BinaryMetricsFormatter<JvmMemoryMetricsV3> {

    @Override
    public Bytes format(List<JvmMemoryMetricsV3> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmMemoryMetricsV3 metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmMemoryMetricsV3 metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_memory_metrics_v3")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(",PoolName=").append(processTagOrField(metrics.getPoolName()))
                .append(" Init=").append(metrics.getInit()).append('i')
                .append(",Used=").append(metrics.getUsed()).append('i')
                .append(",UsedPercent=").append(doubleFormat(metrics.getUsedPercent()))
                .append(",Committed=").append(metrics.getCommitted()).append('i')
                .append(",Max=").append(metrics.getMax()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
