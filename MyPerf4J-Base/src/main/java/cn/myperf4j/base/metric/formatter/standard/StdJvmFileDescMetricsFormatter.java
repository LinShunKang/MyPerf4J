package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.JvmFileDescMetricsFormatter;
import cn.myperf4j.base.util.text.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class StdJvmFileDescMetricsFormatter implements JvmFileDescMetricsFormatter {

    private static final String TITLE_FORMAT = "%-14s%14s%14s%n";

    private static final String DATA_FORMAT = "%-14d%14.2f%14s%n";

    @Override
    public String format(List<JvmFileDescriptorMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (12 * 2 + 24));
        sb.append("MyPerf4J JVM FileDescriptor Metrics [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(TITLE_FORMAT, "OpenCount", "OpenPercent", "MaxPercent"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        for (int i = 0; i < metricsList.size(); ++i) {
            final JvmFileDescriptorMetrics metrics = metricsList.get(i);
            sb.append(
                    String.format(DATA_FORMAT,
                            metrics.getOpenCount(),
                            metrics.getOpenPercent(),
                            metrics.getMaxCount()
                    )
            );
        }
        return sb.toString();
    }
}
