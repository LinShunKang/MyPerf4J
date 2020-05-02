package cn.myperf4j.base.metric.processor.influx;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmCompilationProcessor;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class InfluxJvmCompilationMetricsProcessor extends AbstractJvmCompilationProcessor {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public void process(JvmCompilationMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmCompilationMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_compilation_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(" Time=").append(metrics.getTime()).append('i')
                .append(",TotalTime=").append(metrics.getTotalTime()).append('i')
                .append(' ').append(startNanos);
        return sb.toString();
    }

}
