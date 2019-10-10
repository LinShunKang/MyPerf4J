package cn.myperf4j.base.metric.processor.influx;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmMemoryMetricsProcessor;
import cn.myperf4j.base.util.NumFormatUtils;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class InfluxJvmMemoryMetricsProcessor extends AbstractJvmMemoryMetricsProcessor {

    private ThreadLocal<StringBuilder> sbThreadLocal = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(648);
        }
    };

    @Override
    public void process(JvmMemoryMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = sbThreadLocal.get();
        try {
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmMemoryMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_memory_metrics_v2")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(" HeapUsed=").append(metrics.getHeapUsed()).append('i')
                .append(",HeapUsedPercent=").append(NumFormatUtils.formatDouble(metrics.getHeapUsedPercent()))
                .append(",NonHeapUsed=").append(metrics.getNonHeapUsed()).append('i')
                .append(",NonHeapUsedPercent=").append(NumFormatUtils.formatDouble(metrics.getNonHeapUsedPercent()))
                .append(",PermGenUsed=").append(metrics.getPermGenUsed()).append('i')
                .append(",PermGenUsedPercent=").append(NumFormatUtils.formatDouble(metrics.getPermGenUsedPercent()))
                .append(",MetaspaceUsed=").append(metrics.getMetaspaceUsed()).append('i')
                .append(",MetaspaceUsedPercent=").append(NumFormatUtils.formatDouble(metrics.getMetaspaceUsedPercent()))
                .append(",CodeCacheUsed=").append(metrics.getCodeCacheUsed()).append('i')
                .append(",CodeCacheUsedPercent=").append(NumFormatUtils.formatDouble(metrics.getCodeCacheUsedPercent()))
                .append(",OldGenUsed=").append(metrics.getOldGenUsed()).append('i')
                .append(",OldGenUsedPercent=").append(NumFormatUtils.formatDouble(metrics.getOldGenUsedPercent()))
                .append(",EdenUsed=").append(metrics.getEdenUsed()).append('i')
                .append(",EdenUsedPercent=").append(NumFormatUtils.formatDouble(metrics.getEdenUsedPercent()))
                .append(",SurvivorUsed=").append(metrics.getSurvivorUsed()).append('i')
                .append(",SurvivorUsedPercent=").append(metrics.getSurvivorUsedPercent())
                .append(",ZHeadUsed=").append(metrics.getZHeapUsed()).append('i')
                .append(",ZHeadUsedPercent=").append(metrics.getZHeapUsedPercent())
                .append(",HeapMax=").append(metrics.getHeapMax()).append('i')
                .append(' ').append(startNanos);
        return sb.toString();
    }
}
