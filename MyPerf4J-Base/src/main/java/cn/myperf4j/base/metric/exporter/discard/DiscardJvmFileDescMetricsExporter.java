package cn.myperf4j.base.metric.exporter.discard;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.exporter.JvmFileDescMetricsExporter;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class DiscardJvmFileDescMetricsExporter implements JvmFileDescMetricsExporter {

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void process(JvmFileDescriptorMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

}
