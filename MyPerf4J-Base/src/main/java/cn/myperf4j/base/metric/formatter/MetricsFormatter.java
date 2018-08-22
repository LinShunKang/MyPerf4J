package cn.myperf4j.base.metric.formatter;

import cn.myperf4j.base.metric.Metrics;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/21
 */
public interface MetricsFormatter<T extends Metrics> {

    String format(List<T> metricsList, long startMillis, long stopMillis);

}
