package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.formatter.JvmGcMetricsV3Formatter;
import cn.myperf4j.base.util.text.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2024/02/08
 */
public class StdJvmGcMetricsV3Formatter implements JvmGcMetricsV3Formatter {

    private static final String TITLE_FORMAT = "%-20s%15s%15s%15s%n";

    private static final String DATA_FORMAT = "%-20s%15d%15d%15.2f%n";

    @Override
    public String format(List<JvmGcMetricsV3> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (9 * 3 + 64));
        sb.append("MyPerf4J JVM GC MetricsV3 [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(TITLE_FORMAT, "GcName", "GcCount", "GcTime", "AvgGcTime"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        for (int i = 0; i < metricsList.size(); ++i) {
            final JvmGcMetricsV3 metrics = metricsList.get(i);
            final String gcName = metrics.getGcName();
            final long gcCount = metrics.getGcCount();
            final long gcTime = metrics.getGcTime();
            final double avgGcTime = metrics.getAvgGcTime();
            sb.append(String.format(DATA_FORMAT, gcName, gcCount, gcTime, avgGcTime));
        }
        return sb.toString();
    }
}
