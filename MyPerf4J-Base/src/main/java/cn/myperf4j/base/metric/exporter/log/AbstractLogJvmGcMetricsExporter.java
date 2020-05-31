package cn.myperf4j.base.metric.exporter.log;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.log.ILogger;
import cn.myperf4j.base.log.LoggerFactory;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsExporter;

/**
 * Created by LinShunkang on 2018/8/25
 */
public abstract class AbstractLogJvmGcMetricsExporter implements JvmGcMetricsExporter {

    protected ILogger logger = LoggerFactory.getLogger(ProfilingConfig.metricsConfig().gcMetricsFile());

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        //empty
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        logger.flushLog();
    }
}
