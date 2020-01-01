package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.formatter.JvmFileDescMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/8/21
 */
public final class DefJvmFileDescMetricsFormatter implements JvmFileDescMetricsFormatter {

    @Override
    public String format(List<JvmFileDescriptorMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%-14s%14s%14s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (12 * 2 + 24));
        sb.append("MyPerf4J JVM FileDescriptor Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(dataTitleFormat, "OpenCount", "OpenPercent", "MaxPercent"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-14d%14.2f%14s%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JvmFileDescriptorMetrics metrics = metricsList.get(i);
            sb.append(
                    String.format(dataFormat,
                            metrics.getOpenCount(),
                            metrics.getOpenPercent(),
                            metrics.getMaxCount()
                    )
            );
        }
        return sb.toString();
    }

}
