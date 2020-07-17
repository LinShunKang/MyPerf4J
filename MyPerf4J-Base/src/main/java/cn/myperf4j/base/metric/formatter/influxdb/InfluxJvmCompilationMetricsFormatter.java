package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.formatter.JvmCompilationMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;

/**
 * Created by LinShunkang on 2020/5/17
 */
public final class InfluxJvmCompilationMetricsFormatter implements JvmCompilationMetricsFormatter {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public String format(List<JvmCompilationMetrics> metricsList, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            long startNanos = startMillis * 1000 * 1000L;
            for (int i = 0; i < metricsList.size(); ++i) {
                JvmCompilationMetrics metrics = metricsList.get(i);
                appendLineProtocol(metrics, startNanos, sb);
                sb.append('\n');
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    private void appendLineProtocol(JvmCompilationMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_compilation_metrics")
                .append(",AppName=").append(ProfilingConfig.basicConfig().appName())
                .append(",host=").append(processTagOrField(ProfilingConfig.basicConfig().hostname()))
                .append(" Time=").append(metrics.getTime()).append('i')
                .append(",TotalTime=").append(metrics.getTotalTime()).append('i')
                .append(' ').append(startNanos);
    }

}
