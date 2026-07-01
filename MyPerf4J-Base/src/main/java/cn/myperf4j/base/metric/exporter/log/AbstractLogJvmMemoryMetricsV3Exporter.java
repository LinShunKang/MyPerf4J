package cn.myperf4j.base.metric.exporter.log;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.log.ILogger;
import cn.myperf4j.base.log.LoggerFactory;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsV3Exporter;

/**
 * Created by LinShunkang on 2026/06/30
 */
public abstract class AbstractLogJvmMemoryMetricsV3Exporter implements JvmMemoryMetricsV3Exporter {

    protected ILogger logger = LoggerFactory.getLogger(ProfilingConfig.metricsConfig().memoryMetricsFile());

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        logger.flushLog();
    }
}
