package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JVMThreadMetrics;
import cn.myperf4j.base.metric.formatter.JVMThreadMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DefaultJVMThreadMetricsFormatter implements JVMThreadMetricsFormatter {

    @Override
    public String format(List<JVMThreadMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%-14s%14s%14s%14s%14s%14s%14s%14s%14s%14s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (14 * 10 + 64));
        sb.append("MyPerf4J JVM Thread Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "TotalStarted", "Active", "Peak", "Daemon", "New", "Runnable", "Blocked", "Waiting", "TimedWaiting", "Terminated"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-14s%14d%14d%14d%14d%14d%14d%14d%14d%14d%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JVMThreadMetrics metrics = metricsList.get(i);
            sb.append(String.format(dataFormat,
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
            ));
        }
        return sb.toString();
    }
}
