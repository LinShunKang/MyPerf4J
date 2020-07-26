package cn.myperf4j.base.metric.exporter.log;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.log.ILogger;
import cn.myperf4j.base.log.LoggerFactory;
import cn.myperf4j.base.metric.exporter.JvmFileDescMetricsExporter;

/**
 * Created by LinShunkang on 2019/11/09
 */
public abstract class AbstractLogJvmFileDescMetricsExporter implements JvmFileDescMetricsExporter {

    protected ILogger logger = LoggerFactory.getLogger(ProfilingConfig.metricsConfig().fileDescMetricsFile());

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        logger.flushLog();
    }
}
