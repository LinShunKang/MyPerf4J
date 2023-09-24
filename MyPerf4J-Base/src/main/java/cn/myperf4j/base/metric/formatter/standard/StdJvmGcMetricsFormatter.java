package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.formatter.JvmGcMetricsFormatter;
import cn.myperf4j.base.util.text.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdJvmGcMetricsFormatter implements JvmGcMetricsFormatter {

    private static final String TITLE_FORMAT = "%-15s%15s%15s%15s%15s%15s%15s%15s%15s%15s%18s%18s%18s%18s%n";

    private static final String DATA_FORMAT = "%-15s%15d%15.2f%15d%15d%15d%15d%15.2f%15d%15d%18.2f%18d%18d%18.2f%n";

    @Override
    public String format(List<JvmGcMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (9 * 3 + 64));
        sb.append("MyPerf4J JVM GC Metrics [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(TITLE_FORMAT, "YoungGcCount", "YoungGcTime", "AvgYoungGcTime", "FullGcCount",
                "FullGcTime", "ZGcCount", "ZGcTime", "AvgZGcTime", "ZGcCyclesCount", "ZGcCyclesTime",
                "AvgZGcCyclesTime", "ZGcPausesCount", "ZGcPausesTime", "AvgZGcPausesTime"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        for (int i = 0; i < metricsList.size(); ++i) {
            final JvmGcMetrics metrics = metricsList.get(i);
            sb.append(
                    String.format(DATA_FORMAT,
                            metrics.getYoungGcCount(),
                            metrics.getYoungGcTime(),
                            metrics.getAvgYoungGcTime(),
                            metrics.getFullGcCount(),
                            metrics.getFullGcTime(),
                            metrics.getZGcCount(),
                            metrics.getZGcTime(),
                            metrics.getAvgZGcTime(),
                            metrics.getZGcCyclesCount(),
                            metrics.getZGcCyclesTime(),
                            metrics.getAvgZGcCyclesTime(),
                            metrics.getZGcPausesCount(),
                            metrics.getZGcPausesTime(),
                            metrics.getAvgZGcPausesTime())
            );
        }
        return sb.toString();
    }
}
