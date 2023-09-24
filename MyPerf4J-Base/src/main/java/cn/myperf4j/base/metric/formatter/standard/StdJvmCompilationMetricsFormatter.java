package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.formatter.JvmCompilationMetricsFormatter;
import cn.myperf4j.base.util.text.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class StdJvmCompilationMetricsFormatter implements JvmCompilationMetricsFormatter {

    private static final String TITLE_FORMAT = "%-16s%16s%n";

    private static final String DATA_FORMAT = "%-16d%16d%n";

    @Override
    public String format(List<JvmCompilationMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (16 + 24));
        sb.append("MyPerf4J JVM Compilation Metrics [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(TITLE_FORMAT, "Time(ms)", "TotalTime(ms)"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        for (int i = 0; i < metricsList.size(); ++i) {
            final JvmCompilationMetrics metrics = metricsList.get(i);
            sb.append(String.format(DATA_FORMAT, metrics.getTime(), metrics.getTotalTime()));
        }
        return sb.toString();
    }
}
