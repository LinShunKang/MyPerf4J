package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdJvmBufferPoolMetricsFormatter implements TextMetricsFormatter<JvmBufferPoolMetrics> {

    private static final String TITLE_FORMAT = "MyPerf4J JVM BufferPool Metrics [%s, %s]%n";

    private static final String DATA_TITLE_FORMAT = "%-32s%19s%19s%19s%n";

    private static final String DATA_FORMAT = "%-32s%19d%19d%19d%n";

    @Override
    public String format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append(String.format(TITLE_FORMAT, formatToSeconds(startMillis), formatToSeconds(stopMillis)))
                    .append(String.format(DATA_TITLE_FORMAT, "BufferPool", "BufferCount", "Used", "Capacity"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmBufferPoolMetrics m) {
        return String.format(DATA_FORMAT, m.getName(), m.getBuffCount(), m.getMemoryUsed(), m.getMemoryCapacity());
    }
}
