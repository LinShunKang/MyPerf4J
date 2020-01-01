package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.JvmMemoryMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 1919/8/21
 */
public class DefJvmMemoryMetricsFormatter implements JvmMemoryMetricsFormatter {

    @Override
    public String format(List<JvmMemoryMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%-14s%21s%12s%17s%12s%19s%12s%17s%13s%19s%13s%20s%15s%22s%15s%22s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (9 * 19 + 64));
        sb.append("MyPerf4J JVM Memory Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(dataTitleFormat,
                "SurvivorUsed", "SurvivorUsedPercent",
                "EdenUsed", "EdenUsedPercent",
                "OldGenUsed", "OldGenUsedPercent",
                "HeapUsed", "HeapUsedPercent",
                "NonHeapUsed", "NoHeapUsedPercent",
                "PermGenUsed", "PermGenUsedPercent",
                "MetaspaceUsed", "MetaspaceUsedPercent",
                "CodeCacheUsed", "CodeCacheUsedPercent"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-14d%21.2f%12d%17.2f%12d%19.2f%12d%17.2f%13d%19.2f%13d%20.2f%15d%22.2f%15d%22.2f%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JvmMemoryMetrics metrics = metricsList.get(i);
            sb.append(
                    String.format(dataFormat,
                            metrics.getSurvivorUsed(),
                            metrics.getSurvivorUsedPercent(),
                            metrics.getEdenUsed(),
                            metrics.getEdenUsedPercent(),
                            metrics.getOldGenUsed(),
                            metrics.getOldGenUsedPercent(),
                            metrics.getHeapUsed(),
                            metrics.getHeapUsedPercent(),
                            metrics.getNonHeapUsed(),
                            metrics.getNonHeapUsedPercent(),
                            metrics.getPermGenUsed(),
                            metrics.getPermGenUsedPercent(),
                            metrics.getMetaspaceUsed(),
                            metrics.getMetaspaceUsedPercent(),
                            metrics.getCodeCacheUsed(),
                            metrics.getCodeCacheUsedPercent()
                    )
            );
        }
        return sb.toString();
    }

}
