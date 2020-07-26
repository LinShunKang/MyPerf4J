package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmBufferPoolMetricsFormatter implements JvmBufferPoolMetricsFormatter {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public String format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0; i < metricsList.size(); ++i) {
                JvmBufferPoolMetrics metrics = metricsList.get(i);
                appendLineProtocol(metrics, startNanos, sb);
                sb.append('\n');
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmBufferPoolMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_buffer_pool_metrics_v2")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",PoolName=").append(metrics.getName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" Count=").append(metrics.getCount()).append('i')
                .append(",MemoryUsed=").append(metrics.getMemoryUsed()).append('i')
                .append(",MemoryCapacity=").append(metrics.getMemoryCapacity()).append('i')
                .append(' ').append(startNanos);
    }
}
