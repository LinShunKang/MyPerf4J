package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_ACTIVE;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_BLOCKED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_DAEMON;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_NEW;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_PEAK;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_RUNNABLE;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TERMINATED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TIMED_WAITING;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_TOTAL_STARTED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_WAITING;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmThreadMetricsFormatter implements BinaryMetricsFormatter<JvmThreadMetrics> {

    private static final byte[] MEASUREMENTS = "jvm_thread_metrics".getBytes(UTF_8);

    @Override
    public Bytes format(List<JvmThreadMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmThreadMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmThreadMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(' ')
                .append(F_TOTAL_STARTED).append('=').append(metrics.getTotalStarted()).append('i').append(',')
                .append(F_ACTIVE).append('=').append(metrics.getActive()).append('i').append(',')
                .append(F_PEAK).append('=').append(metrics.getPeak()).append('i').append(',')
                .append(F_DAEMON).append('=').append(metrics.getDaemon()).append('i').append(',')
                .append(F_NEW).append('=').append(metrics.getNews()).append('i').append(',')
                .append(F_RUNNABLE).append('=').append(metrics.getRunnable()).append('i').append(',')
                .append(F_BLOCKED).append('=').append(metrics.getBlocked()).append('i').append(',')
                .append(F_WAITING).append('=').append(metrics.getWaiting()).append('i').append(',')
                .append(F_TIMED_WAITING).append('=').append(metrics.getTimedWaiting()).append('i').append(',')
                .append(F_TERMINATED).append('=').append(metrics.getTerminated()).append('i').append(' ')
                .append(startNanos)
                .append('\n');
    }
}
