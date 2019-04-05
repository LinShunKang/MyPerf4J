package cn.myperf4j.base.metric.formatter.impl;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.JvmBufferPoolMetricsFormatter;
import cn.myperf4j.base.util.DateFormatUtils;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 1919/8/21
 */
public class DefaultJvmBufferPoolMetricsFormatter implements JvmBufferPoolMetricsFormatter {

    @Override
    public String format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        String dataTitleFormat = "%-19s%19s%19s%19s%n";
        StringBuilder sb = new StringBuilder((metricsList.size() + 2) * (3 * 19 + 64));
        sb.append("MyPerf4J JVM BufferPool Metrics [").append(DateFormatUtils.format(startMillis)).append(", ").append(DateFormatUtils.format(stopMillis)).append(']').append(LINE_SEPARATOR);
        sb.append(String.format(dataTitleFormat, "Name", "Count", "MemoryUsed", "MemoryCapacity"));
        if (metricsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-19s%19d%19d%19d%n";
        for (int i = 0; i < metricsList.size(); ++i) {
            JvmBufferPoolMetrics metrics = metricsList.get(i);
            sb.append(String.format(dataFormat,
                    metrics.getName(),
                    metrics.getCount(),
                    metrics.getMemoryUsed(),
                    metrics.getMemoryCapacity()));
        }
        return sb.toString();
    }

}
