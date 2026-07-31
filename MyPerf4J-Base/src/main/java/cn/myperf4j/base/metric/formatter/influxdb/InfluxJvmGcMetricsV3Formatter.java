package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_AVG_GC_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_GC_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_GC_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_GC_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.text.NumFormatUtils.numFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class InfluxJvmGcMetricsV3Formatter implements BinaryMetricsFormatter<JvmGcMetricsV3> {

    private static final byte[] MEASUREMENTS = "jvm_gc_metrics_v3".getBytes(UTF_8);

    @Override
    public Bytes format(List<JvmGcMetricsV3> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmGcMetricsV3 metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmGcMetricsV3 metrics, long startNanos, BytesBuilder bb) {
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(',')
                .append(T_GC_NAME).append('=').append(processTagOrField(metrics.getGcName())).append(' ')
                .append(F_GC_COUNT).append('=').append(metrics.getGcCount()).append('i').append(',')
                .append(F_GC_TIME).append('=').append(metrics.getGcTime()).append('i').append(',')
                .append(F_AVG_GC_TIME).append('=').append(numFormat(metrics.getAvgGcTime())).append(' ')
                .append(startNanos)
                .append('\n');
    }
}
