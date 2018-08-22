package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JVMMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JVMMemoryMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

/**
 * Created by LinShunkang on 1919/8/21
 */
public class DefaultJVMMemoryMetricsFormatter implements JVMMemoryMetricsFormatter {

    @Override
    public String format(List<JVMMemoryMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%-19s%19s%19s%19s%19s%19s%19s%19s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (9 * 19 + 64));
        sb.append("MyPerf4J JVM Memory Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "NonHeapInit", "NonHeapUsed", "NonHeapCommitted", "NonHeapMax", "HeapInit", "HeapUsed", "HeapCommitted", "HeapMax"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-19d%19d%19d%19d%19d%19d%19d%19d%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JVMMemoryMetrics metrics = metricsList.get(i);
            sb.append(String.format(dataFormat,
                    metrics.getNonHeapInit(),
                    metrics.getNonHeapUsed(),
                    metrics.getNonHeapCommitted(),
                    metrics.getNonHeapMax(),
                    metrics.getHeapInit(),
                    metrics.getHeapUsed(),
                    metrics.getHeapCommitted(),
                    metrics.getHeapMax()));
        }
        return sb.toString();
    }

}
