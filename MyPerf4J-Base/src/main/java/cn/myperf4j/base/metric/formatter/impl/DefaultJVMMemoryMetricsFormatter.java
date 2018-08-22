package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JVMMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JVMMemoryMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DefaultJVMMemoryMetricsFormatter implements JVMMemoryMetricsFormatter {

    @Override
    public String format(List<JVMMemoryMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%12s%12s%12s%12s%12s%12s%12s%12s%12s%12s%12s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (9 * 12 + 64));
        sb.append("MyPerf4J JVMMemory Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "NonHeapInit", "NonHeapUsed", "NonHeapCommitted", "NonHeapMax", "HeapInit", "HeapUsed", "HeapCommitted", "HeapMax", "CodeCacheInit", "CodeCacheUsed", "CodeCacheCommitted", "CodeCacheMax"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%12d%12d%12d%12d%12d%12d%12d%12d%12d%12d%12d%12d%n";
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
                    metrics.getHeapMax(),
                    metrics.getCodeCacheInit(),
                    metrics.getCodeCacheUsed(),
                    metrics.getCodeCacheCommitted(),
                    metrics.getCodeCacheMax()));
        }
        return sb.toString();
    }

}
