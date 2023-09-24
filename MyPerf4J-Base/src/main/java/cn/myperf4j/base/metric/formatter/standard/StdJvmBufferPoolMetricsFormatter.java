package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.util.text.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 1919/8/21
 */
public class StdJvmBufferPoolMetricsFormatter implements JvmBufferPoolMetricsFormatter {

    private static final String TITLE_FORMAT = "%-19s%19s%19s%19s%n";

    private static final String DATA_FORMAT = "%-19s%19d%19d%19d%n";

    @Override
    public String format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (3 * 19 + 64));
        sb.append("MyPerf4J JVM BufferPool Metrics [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(TITLE_FORMAT, "Name", "Count", "MemoryUsed", "MemoryCapacity"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        for (int i = 0; i < metricsList.size(); ++i) {
            final JvmBufferPoolMetrics metrics = metricsList.get(i);
            sb.append(
                    String.format(DATA_FORMAT,
                            metrics.getName(),
                            metrics.getCount(),
                            metrics.getMemoryUsed(),
                            metrics.getMemoryCapacity()
                    )
            );
        }
        return sb.toString();
    }
}
