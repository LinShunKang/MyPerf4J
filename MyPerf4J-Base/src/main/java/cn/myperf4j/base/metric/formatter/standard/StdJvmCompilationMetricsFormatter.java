package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class StdJvmCompilationMetricsFormatter implements TextMetricsFormatter<JvmCompilationMetrics> {

    private static final String TITLE_FORMAT = "MyPerf4J JVM Compilation Metrics [%s, %s]%n";

    private static final String DATA_TITLE_FORMAT = "%-16s%16s%n";

    private static final String DATA_FORMAT = "%-16d%16d%n";

    @Override
    public String format(List<JvmCompilationMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append(String.format(TITLE_FORMAT, formatToSeconds(startMillis), formatToSeconds(stopMillis)))
                    .append(String.format(DATA_TITLE_FORMAT, "Time(ms)", "TotalTime(ms)"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmCompilationMetrics m) {
        return String.format(DATA_FORMAT, m.getTime(), m.getTotalTime());
    }
}
