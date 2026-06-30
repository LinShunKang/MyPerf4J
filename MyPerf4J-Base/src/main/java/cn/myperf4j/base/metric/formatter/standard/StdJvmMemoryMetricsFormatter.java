package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;
import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 1919/8/21
 */
public class StdJvmMemoryMetricsFormatter implements TextMetricsFormatter<JvmMemoryMetrics> {

    private static final String TITLE_FORMAT = "%-14s%21s%12s%17s%12s%19s%12s%17s%13s%19s%13s%20s%15s%22s%15s%22s%n";

    private static final String DATA_FORMAT = "%-14d%21.2f%12d%17.2f%12d%19.2f%12d%17.2f%13d%19.2f%13d%20.2f%15d" +
            "%22.2f%15d%22.2f%n";

    @Override
    public String format(List<JvmMemoryMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append("MyPerf4J JVM Memory Metrics [").append(formatToSeconds(startMillis)).append(", ")
                    .append(formatToSeconds(stopMillis)).append(']').append(LINE_SEPARATOR);
            sb.append(String.format(TITLE_FORMAT,
                    "SurvivorUsed", "SurvivorUsedPercent",
                    "EdenUsed", "EdenUsedPercent",
                    "OldGenUsed", "OldGenUsedPercent",
                    "HeapUsed", "HeapUsedPercent",
                    "NonHeapUsed", "NoHeapUsedPercent",
                    "PermGenUsed", "PermGenUsedPercent",
                    "MetaspaceUsed", "MetaspaceUsedPercent",
                    "CodeCacheUsed", "CodeCacheUsedPercent"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmMemoryMetrics m) {
        return String.format(DATA_FORMAT,
                m.getSurvivorUsed(),
                m.getSurvivorUsedPercent(),
                m.getEdenUsed(),
                m.getEdenUsedPercent(),
                m.getOldGenUsed(),
                m.getOldGenUsedPercent(),
                m.getHeapUsed(),
                m.getHeapUsedPercent(),
                m.getNonHeapUsed(),
                m.getNonHeapUsedPercent(),
                m.getPermGenUsed(),
                m.getPermGenUsedPercent(),
                m.getMetaspaceUsed(),
                m.getMetaspaceUsedPercent(),
                m.getCodeCacheUsed(),
                m.getCodeCacheUsedPercent()
        );
    }
}
