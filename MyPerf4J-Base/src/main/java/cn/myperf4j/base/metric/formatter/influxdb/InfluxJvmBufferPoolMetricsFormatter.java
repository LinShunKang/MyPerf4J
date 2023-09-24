package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmBufferPoolMetricsFormatter implements JvmBufferPoolMetricsFormatter {

    @Override
    public String format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            final long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0, size = metricsList.size(); i < size; ++i) {
                appendLineProtocol(metricsList.get(i), startNanos, sb);
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmBufferPoolMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_buffer_pool_metrics_v2")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",PoolName=").append(processTagOrField(metrics.getName()))
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" Count=").append(metrics.getCount()).append('i')
                .append(",MemoryUsed=").append(metrics.getMemoryUsed()).append('i')
                .append(",MemoryCapacity=").append(metrics.getMemoryCapacity()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
