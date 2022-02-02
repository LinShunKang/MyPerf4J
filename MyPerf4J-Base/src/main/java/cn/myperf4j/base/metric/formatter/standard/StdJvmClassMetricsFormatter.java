package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.formatter.JvmClassMetricsFormatter;
import cn.myperf4j.base.util.text.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class StdJvmClassMetricsFormatter implements JvmClassMetricsFormatter {

   private static final String TITLE_FORMAT = "%-10s%10s%10s%n";

    private static final String DATA_FORMAT = "%-10d%10d%10d%n";

    @Override
    public String format(List<JvmClassMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (12 * 3 + 64));
        sb.append("MyPerf4J JVM Class Metrics [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(TITLE_FORMAT, "Total", "Loaded", "Unloaded"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        for (int i = 0; i < metricsList.size(); ++i) {
            final JvmClassMetrics metrics = metricsList.get(i);
            sb.append(
                    String.format(DATA_FORMAT,
                            metrics.getTotal(),
                            metrics.getLoaded(),
                            metrics.getUnloaded()
                    )
            );
        }
        return sb.toString();
    }
}
