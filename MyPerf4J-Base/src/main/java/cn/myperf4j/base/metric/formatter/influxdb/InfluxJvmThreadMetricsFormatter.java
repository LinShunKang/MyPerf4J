package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.BinaryMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;

/**
 * Created by LinShunkang on 2020/5/17
 */
public class InfluxJvmThreadMetricsFormatter implements BinaryMetricsFormatter<JvmThreadMetrics> {

    @Override
    public Bytes format(List<JvmThreadMetrics> metricsList, long startMillis, long stopMillis) {
        try (BytesBuilder bb = BB_TL.get()) {
            final long startNanos = startMillis * 1000 * 1000L;
            for (JvmThreadMetrics metrics : metricsList) {
                appendLineProtocol(metrics, startNanos, bb);
            }
            return bb.toBytes();
        }
    }

    private void appendLineProtocol(JvmThreadMetrics metrics, long startNanos, BytesBuilder bb) {
        bb.append("jvm_thread_metrics")
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
