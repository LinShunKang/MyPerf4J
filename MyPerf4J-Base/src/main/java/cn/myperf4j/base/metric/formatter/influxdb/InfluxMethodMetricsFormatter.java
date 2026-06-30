package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.collections.ListUtils.isEmpty;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxMethodMetricsFormatter implements BinaryMetricsFormatter<MethodMetrics> {

    private static final Bytes EMPTY_BYTES = Bytes.copy(new byte[0]);

    @Override
    public Bytes format(List<MethodMetrics> metricsList, long startMillis, long stopMillis) {
        if (isEmpty(metricsList)) {
            return EMPTY_BYTES;
        }

        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (MethodMetrics methodMetrics : metricsList) {
                appendLineProtocol(methodMetrics, startNanos, bb);
            }
            return bb.toBytes();
        }
    }

    private void appendLineProtocol(MethodMetrics metrics, long startNanos, BytesBuilder bb) {
        final MethodTag methodTag = metrics.getMethodTag();
        final String methodDesc = processTagOrField(methodTag.getSimpleDesc());
        bb.append("method_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",ClassName=").append(methodTag.getSimpleClassName())
                .append(",Method=").append(methodDesc)
                .append(",Type=").append(methodTag.getType())
                .append(",Level=").append(methodTag.getLevel())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" TotalTimePercent=").append(metrics.getTotalTimePercent())
                .append(",RPS=").append(metrics.getRPS()).append('i')
                .append(",Avg=").append(doubleFormat(metrics.getAvgTime()))
                .append(",Min=").append(metrics.getMinTime()).append('i')
                .append(",Max=").append(metrics.getMaxTime()).append('i')
                .append(",StdDev=").append(doubleFormat(metrics.getStdDev()))
                .append(",Count=").append(metrics.getTotalCount()).append('i')
                .append(",TP50=").append(metrics.getTP50()).append('i')
                .append(",TP90=").append(metrics.getTP90()).append('i')
                .append(",TP95=").append(metrics.getTP95()).append('i')
                .append(",TP99=").append(metrics.getTP99()).append('i')
                .append(",TP999=").append(metrics.getTP999()).append('i')
                .append(",TP9999=").append(metrics.getTP9999()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
