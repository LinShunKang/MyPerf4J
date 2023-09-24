package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.JvmThreadMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmThreadMetricsFormatter implements JvmThreadMetricsFormatter {

    @Override
    public String format(List<JvmThreadMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            final long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0; i < metricsList.size(); ++i) {
                appendLineProtocol(metricsList.get(i), startNanos, sb);
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmThreadMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_thread_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(getLocalhostName()))
                .append(" TotalStarted=").append(metrics.getTotalStarted()).append('i')
                .append(",Active=").append(metrics.getActive()).append('i')
                .append(",Peak=").append(metrics.getPeak()).append('i')
                .append(",Daemon=").append(metrics.getDaemon()).append('i')
                .append(",New=").append(metrics.getNews()).append('i')
                .append(",Runnable=").append(metrics.getRunnable()).append('i')
                .append(",Blocked=").append(metrics.getBlocked()).append('i')
                .append(",Waiting=").append(metrics.getWaiting()).append('i')
                .append(",TimedWaiting=").append(metrics.getTimedWaiting()).append('i')
                .append(",Terminated=").append(metrics.getTerminated()).append('i')
                .append(' ').append(startNanos).append('\n');
    }
}
