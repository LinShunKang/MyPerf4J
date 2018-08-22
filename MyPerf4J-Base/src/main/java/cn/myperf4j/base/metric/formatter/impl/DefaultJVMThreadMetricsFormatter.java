package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JVMThreadMetrics;
import cn.myperf4j.base.metric.formatter.JVMThreadMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DefaultJVMThreadMetricsFormatter implements JVMThreadMetricsFormatter {

    /**
     * private int active;
     * private int peak;
     * private int daemon;
     * private long totalStarted;
     */
    @Override
    public String format(List<JVMThreadMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%9s%9s%9s%9s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (9 * 4 + 64));
        sb.append("MyPerf4J JVMThread Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "Active", "Peak", "Daemon", "TotalStarted"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%9s%9d%9d%9d%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JVMThreadMetrics metrics = metricsList.get(i);
            sb.append(String.format(dataFormat,
                    metrics.getActive(),
                    metrics.getPeak(),
                    metrics.getDaemon(),
                    metrics.getTotalStarted()));
        }
        return sb.toString();
    }
}
