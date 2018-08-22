package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JVMClassMetrics;
import cn.myperf4j.base.metric.formatter.JVMClassMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class DefaultJVMClassMetricsFormatter implements JVMClassMetricsFormatter {

    @Override
    public String format(List<JVMClassMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%-10s%10s%10s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (12 * 3 + 64));
        sb.append("MyPerf4J JVM Class Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "Total", "Loaded", "Unloaded"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-10d%10d%10d%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JVMClassMetrics metrics = metricsList.get(i);
            sb.append(String.format(dataFormat,
                    metrics.getTotal(),
                    metrics.getLoaded(),
                    metrics.getUnloaded()));
        }
        return sb.toString();
    }

}
