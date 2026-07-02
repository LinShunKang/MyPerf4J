package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_LOADED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TOTAL;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_UNLOADED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmClassMetricsFormatter implements BinaryMetricsFormatter<JvmClassMetrics> {

    private static final byte[] MEASUREMENTS = "jvm_class_metrics".getBytes(UTF_8);

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
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(' ')
                .append(F_TOTAL).append('=').append(metrics.getTotal()).append('i').append(',')
                .append(F_LOADED).append('=').append(metrics.getLoaded()).append('i').append(',')
                .append(F_UNLOADED).append('=').append(metrics.getUnloaded()).append('i').append(' ')
                .append(startNanos)
                .append('\n');
    }
}
