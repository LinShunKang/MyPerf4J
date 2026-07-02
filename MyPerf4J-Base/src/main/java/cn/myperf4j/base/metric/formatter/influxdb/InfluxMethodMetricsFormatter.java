package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_AVG;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_MAX;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_MIN;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_RPS;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_STD_DEV;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TOTAL_TIME_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TP50;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TP90;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TP95;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TP99;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TP999;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TP9999;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_CLASS_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_LEVEL;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_METHOD;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_TYPE;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.collections.ListUtils.isEmpty;
import static cn.myperf4j.base.util.text.NumFormatUtils.numFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxMethodMetricsFormatter implements BinaryMetricsFormatter<MethodMetrics> {

    private static final Bytes EMPTY_BYTES = Bytes.copy(new byte[0]);

    private static final byte[] MEASUREMENTS = "method_metrics".getBytes(UTF_8);

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
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(MethodMetrics metrics, long startNanos, BytesBuilder bb) {
        final MethodTag methodTag = metrics.getMethodTag();
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(',')
                .append(T_CLASS_NAME).append('=').append(processTagOrField(methodTag.getSimpleClassName())).append(',')
                .append(T_METHOD).append('=').append(processTagOrField(methodTag.getSimpleDesc())).append(',')
                .append(T_TYPE).append('=').append(processTagOrField(methodTag.getType())).append(',')
                .append(T_LEVEL).append('=').append(processTagOrField(methodTag.getLevel())).append(' ')
                .append(F_TOTAL_TIME_PERCENT).append('=').append(metrics.getTotalTimePercent()).append(',')
                .append(F_RPS).append('=').append(metrics.getRPS()).append('i').append(',')
                .append(F_AVG).append('=').append(numFormat(metrics.getAvgTime())).append(',')
                .append(F_MIN).append('=').append(metrics.getMinTime()).append('i').append(',')
                .append(F_MAX).append('=').append(metrics.getMaxTime()).append('i').append(',')
                .append(F_STD_DEV).append('=').append(numFormat(metrics.getStdDev())).append(',')
                .append(F_COUNT).append('=').append(metrics.getTotalCount()).append('i').append(',')
                .append(F_TP50).append('=').append(metrics.getTP50()).append('i').append(',')
                .append(F_TP90).append('=').append(metrics.getTP90()).append('i').append(',')
                .append(F_TP95).append('=').append(metrics.getTP95()).append('i').append(',')
                .append(F_TP99).append('=').append(metrics.getTP99()).append('i').append(',')
                .append(F_TP999).append('=').append(metrics.getTP999()).append('i').append(',')
                .append(F_TP9999).append('=').append(metrics.getTP9999()).append('i').append(' ')
                .append(startNanos)
                .append('\n');
    }
}
