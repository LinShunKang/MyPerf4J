package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JvmMemoryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.NumFormatUtils.doubleFormat;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmMemoryMetricsFormatter implements JvmMemoryMetricsFormatter {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(512);
        }
    };

    @Override
    public String format(List<JvmMemoryMetrics> metricsList, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0; i < metricsList.size(); ++i) {
                JvmMemoryMetrics metrics = metricsList.get(i);
                appendLineProtocol(metrics, startNanos, sb);
                sb.append('\n');
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmMemoryMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_memory_metrics_v2")
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
                .append(' ').append(startNanos);
    }

}
