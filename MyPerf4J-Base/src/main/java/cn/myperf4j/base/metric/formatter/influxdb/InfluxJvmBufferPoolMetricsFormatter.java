package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmBufferPoolMetricsFormatter implements BinaryMetricsFormatter<JvmBufferPoolMetrics> {

    @Override
    public Bytes format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmBufferPoolMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes();
        }
    }

    private void appendLineProtocol(JvmBufferPoolMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_buffer_pool_metrics_v2")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",PoolName=").append(processTagOrField(metrics.getName()))
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" Count=").append(metrics.getCount()).append('i')
                .append(",MemoryUsed=").append(metrics.getMemoryUsed()).append('i')
                .append(",MemoryCapacity=").append(metrics.getMemoryCapacity()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
