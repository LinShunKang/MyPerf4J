package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class StdJvmFileDescMetricsFormatter implements TextMetricsFormatter<JvmFileDescriptorMetrics> {

    private static final String TITLE_FORMAT = "MyPerf4J JVM FileDescriptor Metrics [%s, %s]%n";

    private static final String DATA_TITLE_FORMAT = "%-14s%14s%14s%n";

    private static final String DATA_FORMAT = "%-14d%14.2f%14s%n";

    @Override
    public String format(List<JvmFileDescriptorMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append(String.format(TITLE_FORMAT, formatToSeconds(startMillis), formatToSeconds(stopMillis)))
                    .append(String.format(DATA_TITLE_FORMAT, "OpenCount", "OpenPercent", "MaxCount"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmFileDescriptorMetrics m) {
        return String.format(DATA_FORMAT, m.getOpenCount(), m.getOpenPercent(), m.getMaxCount());
    }
}
