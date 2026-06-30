package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;
import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdJvmThreadMetricsFormatter implements TextMetricsFormatter<JvmThreadMetrics> {

    private static final String TITLE_FORMAT = "%-14s%14s%14s%14s%14s%14s%14s%14s%14s%14s%n";

    private static final String DATA_FORMAT = "%-14s%14d%14d%14d%14d%14d%14d%14d%14d%14d%n";

    @Override
    public String format(List<JvmThreadMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append("MyPerf4J JVM Thread Metrics [").append(formatToSeconds(startMillis)).append(", ")
                    .append(formatToSeconds(stopMillis)).append(']').append(LINE_SEPARATOR);
            sb.append(String.format(TITLE_FORMAT, "TotalStarted", "Active", "Peak", "Daemon", "New", "Runnable",
                    "Blocked", "Waiting", "TimedWaiting", "Terminated"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmThreadMetrics m) {
        return String.format(DATA_FORMAT,
                m.getTotalStarted(),
                m.getActive(),
                m.getPeak(),
                m.getDaemon(),
                m.getNews(),
                m.getRunnable(),
                m.getBlocked(),
                m.getWaiting(),
                m.getTimedWaiting(),
                m.getTerminated()
        );
    }
}
