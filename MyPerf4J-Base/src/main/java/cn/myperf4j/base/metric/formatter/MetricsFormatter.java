package cn.myperf4j.base.metric.formatter;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/21
 */
public interface MetricsFormatter<T, R> {

    R format(List<T> metricsList, long startMillis, long stopMillis);
}
