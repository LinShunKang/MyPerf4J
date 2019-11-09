package cn.myperf4j.base.metric.processor.discard;

import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmCompilationProcessor;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class DiscardJvmCompilationProcessor extends AbstractJvmCompilationProcessor {

    @Override
    public void process(JvmCompilationMetrics metrics, long processId, long startMillis, long stopMillis) {
        //empty
    }

}
