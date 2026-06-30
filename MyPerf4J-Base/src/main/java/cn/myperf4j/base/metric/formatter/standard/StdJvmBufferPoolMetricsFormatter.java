package cn.myperf4j.base.metric.formatter.standard;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.formatter.TextMetricsFormatter;

import java.util.List;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;
import static cn.myperf4j.base.util.text.DateFormatUtils.formatToSeconds;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class StdJvmBufferPoolMetricsFormatter implements TextMetricsFormatter<JvmBufferPoolMetrics> {

    private static final String TITLE_FORMAT = "%-32s%19s%19s%19s%n";

    private static final String DATA_FORMAT = "%-32s%19d%19d%19d%n";

    //TODO:LSK 可以做以下两个优化，定义 Title 是第一行，DataTitle 是第二行
    // 1. 把 TITLE_FORMAT 改成 DATA_TITLE_FORMAT，并且提前生成 DATA_TITLE；
    // 2. 把 TITLE 提炼出 TITLE_FORMAT，并在代码里使用 String#format 方法进行格式化，提升代码可读性；
    // 以上优化可以应用到所有 TextMetricsFormatter 中！

    @Override
    public String format(List<JvmBufferPoolMetrics> metricsList, long startMillis, long stopMillis) {
        final StringBuilder sb = SB_TL.get();
        try {
            sb.append("MyPerf4J JVM BufferPool Metrics [").append(formatToSeconds(startMillis)).append(", ")
                    .append(formatToSeconds(stopMillis)).append(']').append(LINE_SEPARATOR);
            sb.append(String.format(TITLE_FORMAT, "Name", "Count", "MemoryUsed", "MemoryCapacity"));
            metricsList.forEach(m -> sb.append(formatData(m)));
            return sb.toString();
        } finally {
            sb.setLength(0);
        }
    }

    private String formatData(JvmBufferPoolMetrics m) {
        return String.format(DATA_FORMAT, m.getName(), m.getCount(), m.getMemoryUsed(), m.getMemoryCapacity());
    }
}
