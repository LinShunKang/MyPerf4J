package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;
import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class StdJvmGcMetricsV3Formatter implements TextMetricsFormatter<JvmGcMetricsV3> {

    private static final String TITLE_FORMAT = "%-20s%15s%15s%15s%n";

    private static final String DATA_FORMAT = "%-20s%15d%15d%15.2f%n";

    @Override
    public String format(List<JvmGcMetricsV3> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append("MyPerf4J JVM GC MetricsV3 [").append(formatToSeconds(startMillis)).append(", ")
                    .append(formatToSeconds(stopMillis)).append(']').append(LINE_SEPARATOR);
            sb.append(String.format(TITLE_FORMAT, "GcName", "GcCount", "GcTime", "AvgGcTime"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmGcMetricsV3 m) {
        return String.format(DATA_FORMAT, m.getGcName(), m.getGcCount(), m.getGcTime(), m.getAvgGcTime());
    }
}
