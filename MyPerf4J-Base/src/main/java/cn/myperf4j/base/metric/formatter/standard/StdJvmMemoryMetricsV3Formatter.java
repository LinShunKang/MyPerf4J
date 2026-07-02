package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2026/06/30
 */
public class StdJvmMemoryMetricsV3Formatter implements TextMetricsFormatter<JvmMemoryMetricsV3> {

    private static final String TITLE_FORMAT = "MyPerf4J JVM Memory MetricsV3 [%s, %s]%n";

    private static final String DATA_TITLE_FORMAT = "%-24s%16s%16s%16s%16s%n";

    private static final String DATA_FORMAT = "%-24s%16d%16.2f%16d%16d%n";

    @Override
    public String format(List<JvmMemoryMetricsV3> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append(String.format(TITLE_FORMAT, formatToSeconds(startMillis), formatToSeconds(stopMillis)))
                    .append(String.format(DATA_TITLE_FORMAT, "MemoryPool", "Used", "UsedPercent", "Committed", "Max"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmMemoryMetricsV3 m) {
        return String.format(DATA_FORMAT,
                m.getPoolName(),
                m.getUsed(),
                m.getUsedPercent(),
                m.getCommitted(),
                m.getMax());
    }
}
