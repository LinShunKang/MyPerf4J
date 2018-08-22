package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JVMGCMetrics;
import cn.myperf4j.base.metric.formatter.JVMGCMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DefaultJVMGCMetricsFormatter implements JVMGCMetricsFormatter {

    @Override
    public String format(List<JVMGCMetrics> metricsList, long startMillis, long stopMillis) {
        int[] statisticsArr = getStatistics(metricsList);
        int maxGCNameLength = statisticsArr[0];

        String dataTitleFormat = "%-" + maxGCNameLength + "s%9s%9s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (9 * 3 + 64));
        sb.append("MyPerf4J JVM GC Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "Name", "Count", "Time"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-" + maxGCNameLength + "s%9d%9d%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JVMGCMetrics metrics = metricsList.get(i);
            sb.append(String.format(dataFormat,
                    metrics.getGcName(),
                    metrics.getCollectCount(),
                    metrics.getCollectTime()));
        }
        return sb.toString();
    }

    /**
     * @param metricsList
     * @return : int[0]:max(api.length)
     */
    private int[] getStatistics(List<JVMGCMetrics> metricsList) {
        int[] result = {1};
        for (int i = 0; i < metricsList.size(); ++i) {
            JVMGCMetrics metrics = metricsList.get(i);
            if (metrics == null) {
                continue;
            }

            result[0] = Math.max(result[0], metrics.getGcName().length());
        }
        return result;
    }
}
