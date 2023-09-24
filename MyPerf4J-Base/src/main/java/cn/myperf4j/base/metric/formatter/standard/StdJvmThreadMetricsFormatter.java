package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.formatter.JvmThreadMetricsFormatter;
import cn.myperf4j.base.util.text.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdJvmThreadMetricsFormatter implements JvmThreadMetricsFormatter {

    private static final String TITLE_FORMAT = "%-14s%14s%14s%14s%14s%14s%14s%14s%14s%14s%n";

    private static final String DATA_FORMAT = "%-14s%14d%14d%14d%14d%14d%14d%14d%14d%14d%n";

    @Override
    public String format(List<JvmThreadMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (14 * 10 + 64));
        sb.append("MyPerf4J JVM Thread Metrics [").append(DateFormatUtils.format(startMillis)).append(", ")
                .append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(TITLE_FORMAT, "TotalStarted", "Active", "Peak", "Daemon", "New", "Runnable",
                "Blocked", "Waiting", "TimedWaiting", "Terminated"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        for (int i = 0; i < metricsList.size(); ++i) {
            final JvmThreadMetrics metrics = metricsList.get(i);
            sb.append(
                    String.format(DATA_FORMAT,
                            metrics.getTotalStarted(),
                            metrics.getActive(),
                            metrics.getPeak(),
                            metrics.getDaemon(),
                            metrics.getNews(),
                            metrics.getRunnable(),
                            metrics.getBlocked(),
                            metrics.getWaiting(),
                            metrics.getTimedWaiting(),
                            metrics.getTerminated()
                    )
            );
        }
        return sb.toString();
    }
}
