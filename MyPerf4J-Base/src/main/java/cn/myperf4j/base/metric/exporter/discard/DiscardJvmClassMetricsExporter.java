package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.exporter.JvmClassMetricsExporter;

/**
 * Created by LinShunkang on 2018/8/21
 */
public class DiscardJvmClassMetricsExporter implements JvmClassMetricsExporter {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmClassMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

}
