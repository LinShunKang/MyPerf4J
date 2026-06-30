package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdJvmGcMetricsFormatter implements TextMetricsFormatter<JvmGcMetrics> {

    private static final String TITLE_FORMAT = "MyPerf4J JVM GC Metrics [%s, %s]%n";

    private static final String DATA_TITLE_FORMAT = "%-15s%15s%15s%15s%15s%15s%15s%15s%15s%15s%18s%18s%18s%18s%n";

    private static final String DATA_FORMAT = "%-15s%15d%15.2f%15d%15d%15d%15d%15.2f%15d%15d%18.2f%18d%18d%18.2f%n";

    @Override
    public String format(List<JvmGcMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append(String.format(TITLE_FORMAT, formatToSeconds(startMillis), formatToSeconds(stopMillis)))
                    .append(String.format(DATA_TITLE_FORMAT,
                            "YoungGcCount", "YoungGcTime", "AvgYoungGcTime", "FullGcCount",
                            "FullGcTime", "ZGcCount", "ZGcTime", "AvgZGcTime", "ZGcCyclesCount", "ZGcCyclesTime",
                            "AvgZGcCyclesTime", "ZGcPausesCount", "ZGcPausesTime", "AvgZGcPausesTime")
                    );
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmGcMetrics m) {
        return String.format(DATA_FORMAT,
                m.getYoungGcCount(),
                m.getYoungGcTime(),
                m.getAvgYoungGcTime(),
                m.getFullGcCount(),
                m.getFullGcTime(),
                m.getZGcCount(),
                m.getZGcTime(),
                m.getAvgZGcTime(),
                m.getZGcCyclesCount(),
                m.getZGcCyclesTime(),
                m.getAvgZGcCyclesTime(),
                m.getZGcPausesCount(),
                m.getZGcPausesTime(),
                m.getAvgZGcPausesTime());
    }
}
