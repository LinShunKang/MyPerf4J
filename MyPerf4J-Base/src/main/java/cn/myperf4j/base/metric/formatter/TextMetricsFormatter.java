package cn.myperf4j.base.metric.formatter;

import cn.myperf4j.base.metric.Metrics;

/**
 * Created by LinShunkang on 2026/06/16
 */
public interface TextMetricsFormatter<T extends Metrics> extends MetricsFormatter<T, String> {

    ThreadLocal<StringBuilder> SB_TL = ThreadLocal.withInitial(() -> new StringBuilder(32 * 1024));
}
