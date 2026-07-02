package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_COMMITTED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_INIT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_MAX;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_POOL_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.text.NumFormatUtils.numFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class InfluxJvmMemoryMetricsV3Formatter implements BinaryMetricsFormatter<JvmMemoryMetricsV3> {

    private static final byte[] MEASUREMENTS = "jvm_memory_metrics_v3".getBytes(UTF_8);

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
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(',')
                .append(T_POOL_NAME).append('=').append(processTagOrField(metrics.getPoolName())).append(' ')
                .append(F_INIT).append('=').append(metrics.getInit()).append('i').append(',')
                .append(F_USED).append('=').append(metrics.getUsed()).append('i').append(',')
                .append(F_USED_PERCENT).append('=').append(numFormat(metrics.getUsedPercent())).append(',')
                .append(F_COMMITTED).append('=').append(metrics.getCommitted()).append('i').append(',')
                .append(F_MAX).append('=').append(metrics.getMax()).append('i').append(' ')
                .append(startNanos)
                .append('\n');
    }
}
