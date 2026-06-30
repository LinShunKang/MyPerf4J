package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;
import cn.myperf4j.base.util.text.NumFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;
import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/3/30
 */
public final class StdMethodMetricsFormatter implements TextMetricsFormatter<MethodMetrics> {

    @Override
    public String format(List<MethodMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            final int maxApiLength = getMaxApiLength(metricsList);
            final String dataTitleFormat = "%-" + maxApiLength + "s%13s%13s%13s%9s%9s%9s%9s%10s%9s%9s%9s%9s%9s%9s%9s%n";
            sb.append("MyPerf4J Method Metrics [").append(formatToSeconds(startMillis)).append(", ")
                    .append(formatToSeconds(stopMillis)).append(']').append(LINE_SEPARATOR);
            sb.append(String.format(dataTitleFormat, "Method[" + metricsList.size() + "]", "Type", "Level",
                    "TimePercent", "RPS", "Avg(ms)", "Min(ms)", "Max(ms)", "StdDev", "Count", "TP50", "TP90", "TP95",
                    "TP99", "TP999", "TP9999"));

            final String dataFormat = "%-" + maxApiLength + "s%13s%13s%13s%9d%9.2f%9d%9d%9.2f%10d%9d%9d%9d%9d%9d%9d%n";
            sortByTotalTime(metricsList);
            metricsList.forEach(m -> sb.append(formatData(dataFormat, m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(String dataFormat, MethodMetrics metrics) {
        return String.format(dataFormat,
                metrics.getMethodTag().getSimpleDesc(),
                metrics.getMethodTag().getType(),
                metrics.getMethodTag().getLevel(),
                NumFormatUtils.doublePercent(metrics.getTotalTimePercent()),
                metrics.getRPS(),
                metrics.getAvgTime(),
                metrics.getMinTime(),
                metrics.getMaxTime(),
                metrics.getStdDev(),
                metrics.getTotalCount(),
                metrics.getTP50(),
                metrics.getTP90(),
                metrics.getTP95(),
                metrics.getTP99(),
                metrics.getTP999(),
                metrics.getTP9999()
        );
    }

    private int getMaxApiLength(List<MethodMetrics> metricsList) {
        int result = 1;
        for (final MethodMetrics metrics : metricsList) {
            if (metrics != null && metrics.getMethodTag() != null) {
                result = Math.max(result, metrics.getMethodTag().getSimpleDesc().length());
            }
        }
        return result;
    }

    private void sortByTotalTime(List<MethodMetrics> metricsList) {
        metricsList.sort((o1, o2) -> Long.compare(o2.getTotalTime(), o1.getTotalTime()));
    }
}
