package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_CODE_CACHE_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_CODE_CACHE_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_EDEN_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_EDEN_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_HEAP_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_HEAP_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_METASPACE_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_METASPACE_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_NON_HEAP_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_NON_HEAP_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_OLD_GEN_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_OLD_GEN_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_PERM_GEN_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_PERM_GEN_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_SURVIVOR_USED;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBFields.F_SURVIVOR_USED_PERCENT;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBTags.T_HOST;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_APP_NAME;
import static cn.myperf4j.base.metric.formatter.influxdb.InfluxDBValues.V_HOST;
import static cn.myperf4j.base.util.text.NumFormatUtils.numFormat;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmMemoryMetricsFormatter implements BinaryMetricsFormatter<JvmMemoryMetrics> {

    private static final byte[] MEASUREMENTS = "jvm_memory_metrics_v2".getBytes(UTF_8);

    @Override
    public Bytes format(List<JvmMemoryMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmMemoryMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes(Math.max(0, bb.getCount() - 1));
        }
    }

    private void appendLineProtocol(JvmMemoryMetrics m, long startNanos, BytesBuilder bb) {
        bb.append(MEASUREMENTS).append(',')
                .append(T_APP_NAME).append('=').append(V_APP_NAME).append(',')
                .append(T_HOST).append('=').append(V_HOST).append(' ')
                .append(F_HEAP_USED).append('=').append(m.getHeapUsed()).append('i').append(',')
                .append(F_HEAP_USED_PERCENT).append('=').append(numFormat(m.getHeapUsedPercent())).append(',')
                .append(F_NON_HEAP_USED).append('=').append(m.getNonHeapUsed()).append('i').append(',')
                .append(F_NON_HEAP_USED_PERCENT).append('=').append(numFormat(m.getNonHeapUsedPercent())).append(',')
                .append(F_PERM_GEN_USED).append('=').append(m.getPermGenUsed()).append('i').append(',')
                .append(F_PERM_GEN_USED_PERCENT).append('=').append(numFormat(m.getPermGenUsedPercent())).append(',')
                .append(F_METASPACE_USED).append('=').append(m.getMetaspaceUsed()).append('i').append(',')
                .append(F_METASPACE_USED_PERCENT).append('=').append(numFormat(m.getMetaspaceUsedPercent())).append(',')
                .append(F_CODE_CACHE_USED).append('=').append(m.getCodeCacheUsed()).append('i').append(',')
                .append(F_CODE_CACHE_USED_PERCENT).append('=').append(numFormat(m.getCodeCacheUsedPercent()))
                .append(',')
                .append(F_OLD_GEN_USED).append('=').append(m.getOldGenUsed()).append('i').append(',')
                .append(F_OLD_GEN_USED_PERCENT).append('=').append(numFormat(m.getOldGenUsedPercent())).append(',')
                .append(F_EDEN_USED).append('=').append(m.getEdenUsed()).append('i').append(',')
                .append(F_EDEN_USED_PERCENT).append('=').append(numFormat(m.getEdenUsedPercent())).append(',')
                .append(F_SURVIVOR_USED).append('=').append(m.getSurvivorUsed()).append('i').append(',')
                .append(F_SURVIVOR_USED_PERCENT).append('=').append(m.getSurvivorUsedPercent()).append(' ')
                .append(startNanos)
                .append('\n');
    }
}
