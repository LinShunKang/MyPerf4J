package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.exporter.JvmCompilationMetricsExporter;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class DiscardJvmCompilationMetricsExporter implements JvmCompilationMetricsExporter {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmCompilationMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }
}
