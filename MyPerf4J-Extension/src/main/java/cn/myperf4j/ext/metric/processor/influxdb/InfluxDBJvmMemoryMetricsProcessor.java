package cn.myperf4j.ext.metric.processor.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.processor.impl.AbstractJvmMemoryMetricsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxDBJvmMemoryMetricsProcessor extends AbstractJvmMemoryMetricsProcessor {

    private Logger logger = LoggerFactory.getLogger(InfluxDBJvmMemoryMetricsProcessor.class);

    private ThreadLocal<StringBuilder> sbThreadLocal = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(256);
        }
    };

    @Override
    public void process(JvmMemoryMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = sbThreadLocal.get();
        try {
            logger.info(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmMemoryMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_memory_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(" NonHeapInit=").append(metrics.getNonHeapInit())
                .append(",NonHeapUsed=").append(metrics.getNonHeapUsed())
                .append(",NonHeapCommitted=").append(metrics.getNonHeapCommitted())
                .append(",NonHeapMax=").append(metrics.getNonHeapMax())
                .append(",HeapInit=").append(metrics.getHeapInit())
                .append(",HeapUsed=").append(metrics.getHeapUsed())
                .append(",HeapCommitted=").append(metrics.getHeapCommitted())
                .append(",HeapMax=").append(metrics.getHeapMax())
                .append(" ").append(startNanos);
        return sb.toString();
    }
}
