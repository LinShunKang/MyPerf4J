package cn.myperf4j.base.metric.processor;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.log.ILogger;
import cn.myperf4j.base.log.LoggerFactory;

/**
 * Created by LinShunkang on 2018/8/25
 */
public abstract class AbstractJvmBufferPoolMetricsProcessor implements JvmBufferPoolMetricsProcessor {

    protected ILogger logger = LoggerFactory.getLogger(ProfilingConfig.getInstance().getBufferPoolMetricsFile());

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        logger.flushLog();
    }
}
