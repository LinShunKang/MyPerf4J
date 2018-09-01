package cn.myperf4j.ext.metric.processor.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.processor.impl.AbstractJvmThreadMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxDBJvmThreadMetricsProcessor extends AbstractJvmThreadMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(InfluxDBJvmThreadMetricsProcessor.class);

    private ThreadLocal<StringBuilder> sbThreadLocal = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(256);
        }
    };

    @Override
    public void process(JvmThreadMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = sbThreadLocal.get();
        try {
            logger.info(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmThreadMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_thread_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(" TotalStarted=").append(metrics.getTotalStarted()).append("i")
                .append(",Active=").append(metrics.getActive()).append("i")
                .append(",Peak=").append(metrics.getPeak()).append("i")
                .append(",Daemon=").append(metrics.getDaemon()).append("i")
                .append(",New=").append(metrics.getNews()).append("i")
                .append(",Runnable=").append(metrics.getRunnable()).append("i")
                .append(",Blocked=").append(metrics.getBlocked()).append("i")
                .append(",Waiting=").append(metrics.getWaiting()).append("i")
                .append(",TimedWaiting=").append(metrics.getTimedWaiting()).append("i")
                .append(",Terminated=").append(metrics.getTerminated()).append("i")
                .append(" ").append(startNanos);
        return sb.toString();
    }
}
