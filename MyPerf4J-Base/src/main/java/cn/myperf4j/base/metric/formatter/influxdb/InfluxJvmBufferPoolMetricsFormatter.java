package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_MEMORY_CAPACITY;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_MEMORY_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_POOL_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmBufferPoolMetricsFormatter implements BinaryMetricsFormatter<JvmBufferPoolMetrics> {

    private static final byte[] MEASUREMENTS = "jvm_buffer_pool_metrics_v2".getBytes(UTF_8);

    @Override
    public Bytes format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmBufferPoolMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmBufferPoolMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(',')
                .append(T_POOL_NAME).append('=').append(processTagOrField(metrics.getName())).append(' ')
                .append(F_COUNT).append('=').append(metrics.getBuffCount()).append('i').append(',')
                .append(F_MEMORY_USED).append('=').append(metrics.getMemoryUsed()).append('i').append(',')
                .append(F_MEMORY_CAPACITY).append('=').append(metrics.getMemoryCapacity()).append('i').append(' ')
                .append(startNanos)
                .append('\n');
    }
}
