package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.metric.formatter.MethodMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/30
 */
public final class DefaultMethodMetricsFormatter implements MethodMetricsFormatter {

    @Override
    public String format(List<MethodMetrics> methodMetricsList, long startMillis, long stopMillis) {
        int[] statisticsArr = getStatistics(methodMetricsList);
        int maxApiLength = statisticsArr[0];

        String dataTitleFormat = "%-" + maxApiLength + "s%13s%9s%9s%9s%9s%9s%10s%9s%9s%9s%9s%9s%9s%9s%9s%n";
        StringBuilder sb = new StringBuilder((methodMetricsList.size() + 2) * (9 * 11 + 1 + maxApiLength));
        sb.append("MyPerf4J Method Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "Method[" + methodMetricsList.size() + "]", "Type", "RPS", "Avg(ms)", "Min(ms)", "Max(ms)", "StdDev", "Count", "TP50", "TP90", "TP95", "TP99", "TP999", "TP9999", "TP99999", "TP100"));
        if (methodMetricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-" + maxApiLength + "s%13s%9d%9.2f%9d%9d%9.2f%10d%9d%9d%9d%9d%9d%9d%9d%9d%n";
        for (int i = 0; i < methodMetricsList.size(); ++i) {
            MethodMetrics metrics = methodMetricsList.get(i);
            if (metrics.getTotalCount() <= 0) {
                continue;
            }

            sb.append(String.format(dataFormat,
                    metrics.getMethodTag().getSimpleDesc(),
                    metrics.getMethodTag().getType(),
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
                    metrics.getTP9999(),
                    metrics.getTP99999(),
                    metrics.getTP100()));
        }
        return sb.toString();
    }

    /**
     * @param methodMetricsList
     * @return : int[0]:max(api.length)
     */
    private int[] getStatistics(List<MethodMetrics> methodMetricsList) {
        int[] result = {1};
        for (int i = 0; i < methodMetricsList.size(); ++i) {
            MethodMetrics stats = methodMetricsList.get(i);
            if (stats == null || stats.getMethodTag() == null) {
                continue;
            }

            result[0] = Math.max(result[0], stats.getMethodTag().getSimpleDesc().length());
        }
        return result;
    }

}
