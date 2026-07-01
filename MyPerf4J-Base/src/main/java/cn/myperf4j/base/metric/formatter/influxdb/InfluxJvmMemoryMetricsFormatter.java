package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmMemoryMetricsFormatter implements BinaryMetricsFormatter<JvmMemoryMetrics> {

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

    private void appendLineProtocol(JvmMemoryMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_memory_metrics_v2")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" HeapUsed=").append(metrics.getHeapUsed()).append('i')
                .append(",HeapUsedPercent=").append(doubleFormat(metrics.getHeapUsedPercent()))
                .append(",NonHeapUsed=").append(metrics.getNonHeapUsed()).append('i')
                .append(",NonHeapUsedPercent=").append(doubleFormat(metrics.getNonHeapUsedPercent()))
                .append(",PermGenUsed=").append(metrics.getPermGenUsed()).append('i')
                .append(",PermGenUsedPercent=").append(doubleFormat(metrics.getPermGenUsedPercent()))
                .append(",MetaspaceUsed=").append(metrics.getMetaspaceUsed()).append('i')
                .append(",MetaspaceUsedPercent=").append(doubleFormat(metrics.getMetaspaceUsedPercent()))
                .append(",CodeCacheUsed=").append(metrics.getCodeCacheUsed()).append('i')
                .append(",CodeCacheUsedPercent=").append(doubleFormat(metrics.getCodeCacheUsedPercent()))
                .append(",OldGenUsed=").append(metrics.getOldGenUsed()).append('i')
                .append(",OldGenUsedPercent=").append(doubleFormat(metrics.getOldGenUsedPercent()))
                .append(",EdenUsed=").append(metrics.getEdenUsed()).append('i')
                .append(",EdenUsedPercent=").append(doubleFormat(metrics.getEdenUsedPercent()))
                .append(",SurvivorUsed=").append(metrics.getSurvivorUsed()).append('i')
                .append(",SurvivorUsedPercent=").append(metrics.getSurvivorUsedPercent())
                .append(' ').append(startNanos).append('\n');
    }
}
