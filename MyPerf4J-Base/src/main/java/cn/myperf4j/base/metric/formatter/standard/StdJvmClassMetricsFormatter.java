package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;
import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class StdJvmClassMetricsFormatter implements TextMetricsFormatter<JvmClassMetrics> {

    private static final String TITLE_FORMAT = "%-10s%10s%10s%n";

    private static final String DATA_FORMAT = "%-10d%10d%10d%n";

    @Override
    public String format(List<JvmClassMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append("MyPerf4J JVM Class Metrics [").append(formatToSeconds(startMillis)).append(", ")
                    .append(formatToSeconds(stopMillis)).append(']').append(LINE_SEPARATOR);
            sb.append(String.format(TITLE_FORMAT, "Total", "Loaded", "Unloaded"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmClassMetrics m) {
        return String.format(DATA_FORMAT, m.getTotal(), m.getLoaded(), m.getUnloaded());
    }
}
