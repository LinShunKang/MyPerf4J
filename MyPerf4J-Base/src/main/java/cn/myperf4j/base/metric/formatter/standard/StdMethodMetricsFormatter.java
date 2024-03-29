package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.MethodMetricsFormatter;
import cn.myperf4j.base.util.text.DateFormatUtils;
import cn.myperf4j.base.util.text.NumFormatUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/3/30
 */
public final class StdMethodMetricsFormatter implements MethodMetricsFormatter {

    @Override
    public String format(List<MethodMetrics> methodMetricsList, long startMillis, long stopMillis) {
        final int maxApiLength = getMaxApiLength(methodMetricsList);
        final String dataTitleFormat = "%-" + maxApiLength + "s%13s%13s%13s%9s%9s%9s%9s%10s%9s%9s%9s%9s%9s%9s%9s%n";
        final StringBuilder sb = new StringBuilder((methodMetricsList.size() + 2) * (9 * 11 + 1 + maxApiLength));
        sb.append("MyPerf4J Method Metrics [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(dataTitleFormat, "Method[" + methodMetricsList.size() + "]", "Type", "Level",
                "TimePercent", "RPS", "Avg(ms)", "Min(ms)", "Max(ms)", "StdDev", "Count", "TP50", "TP90", "TP95",
                "TP99", "TP999", "TP9999"));
        if (methodMetricsList.isEmpty()) {
            return sb.toString();
        }
        sortByTotalTime(methodMetricsList);

        final String dataFormat = "%-" + maxApiLength + "s%13s%13s%13s%9d%9.2f%9d%9d%9.2f%10d%9d%9d%9d%9d%9d%9d%n";
        for (int i = 0; i < methodMetricsList.size(); ++i) {
            final MethodMetrics metrics = methodMetricsList.get(i);
            if (metrics.getTotalCount() <= 0) {
                continue;
            }

            sb.append(
                    String.format(dataFormat,
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
                    )
            );
        }
        return sb.toString();
    }

    private int getMaxApiLength(List<MethodMetrics> methodMetricsList) {
        int result = 1;
        for (int i = 0; i < methodMetricsList.size(); ++i) {
            final MethodMetrics stats = methodMetricsList.get(i);
            if (stats != null && stats.getMethodTag() != null) {
                result = Math.max(result, stats.getMethodTag().getSimpleDesc().length());
            }
        }
        return result;
    }

    private void sortByTotalTime(List<MethodMetrics> methodMetricsList) {
        Collections.sort(methodMetricsList, new Comparator<MethodMetrics>() {
            @Override
            public int compare(MethodMetrics o1, MethodMetrics o2) {
                return Long.compare(o2.getTotalTime(), o1.getTotalTime());
            }
        });
    }
}
