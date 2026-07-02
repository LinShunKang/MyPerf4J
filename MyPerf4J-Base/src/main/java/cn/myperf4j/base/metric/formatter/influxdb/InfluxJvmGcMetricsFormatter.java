package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_AVG_YOUNG_GC_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_AVG_ZGC_CYCLES_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_AVG_ZGC_PAUSES_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_AVG_ZGC_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_FULL_GC_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_FULL_GC_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_YOUNG_GC_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_YOUNG_GC_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_ZGC_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_ZGC_CYCLES_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_ZGC_CYCLES_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_ZGC_PAUSES_COUNT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_ZGC_PAUSES_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_ZGC_TIME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static cn.myperf4j.base.util.text.NumFormatUtils.numFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmGcMetricsFormatter implements BinaryMetricsFormatter<JvmGcMetrics> {

    private static final byte[] MEASUREMENTS = "jvm_gc_metrics_v2".getBytes(UTF_8);

    @Override
    public Bytes format(List<JvmGcMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmGcMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmGcMetrics m, long startNanos, BytesBuilder bb) {
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(' ')
                .append(F_YOUNG_GC_COUNT).append('=').append(m.getYoungGcCount()).append('i').append(',')
                .append(F_YOUNG_GC_TIME).append('=').append(m.getYoungGcTime()).append('i').append(',')
                .append(F_AVG_YOUNG_GC_TIME).append('=').append(numFormat(m.getAvgYoungGcTime())).append(',')
                .append(F_FULL_GC_COUNT).append('=').append(m.getFullGcCount()).append('i').append(',')
                .append(F_FULL_GC_TIME).append('=').append(m.getFullGcTime()).append('i').append(',')
                .append(F_ZGC_TIME).append('=').append(m.getZGcTime()).append('i').append(',')
                .append(F_ZGC_COUNT).append('=').append(m.getZGcCount()).append('i').append(',')
                .append(F_AVG_ZGC_TIME).append('=').append(numFormat(m.getAvgZGcTime())).append(',')
                .append(F_ZGC_CYCLES_TIME).append('=').append(m.getZGcCyclesTime()).append('i').append(',')
                .append(F_ZGC_CYCLES_COUNT).append('=').append(m.getZGcCyclesCount()).append('i').append(',')
                .append(F_AVG_ZGC_CYCLES_TIME).append('=').append(numFormat(m.getAvgZGcCyclesTime())).append(',')
                .append(F_ZGC_PAUSES_TIME).append('=').append(m.getZGcPausesTime()).append('i').append(',')
                .append(F_ZGC_PAUSES_COUNT).append('=').append(m.getZGcPausesCount()).append('i').append(',')
                .append(F_AVG_ZGC_PAUSES_TIME).append('=').append(numFormat(m.getAvgZGcPausesTime())).append(' ')
                .append(startNanos)
                .append('\n');
    }
}
