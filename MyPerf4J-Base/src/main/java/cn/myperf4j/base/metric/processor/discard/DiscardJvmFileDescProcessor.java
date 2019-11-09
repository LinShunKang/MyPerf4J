package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmFileDescProcessor;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class DiscardJvmFileDescProcessor extends AbstractJvmFileDescProcessor {

    @Override
    public void process(JvmFileDescriptorMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

}
