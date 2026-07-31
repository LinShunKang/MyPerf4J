package cn.myperf4j.base.metric.formatter;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.io.BytesBuilder;
import cn.myperf4j.base.metric.Metrics;

/**
 * Created by LinShunkang on 2026/06/16
 */
public interface BinaryMetricsFormatter<T extends Metrics> extends MetricsFormatter<T, Bytes> {

    ThreadLocal<BytesBuilder> BB_TL = ThreadLocal.withInitial(() -> new BytesBuilder(64 * 1024));
}
