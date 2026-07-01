package cn.myperf4j.core.scheduler;

import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.constant.PropertyValues.Metrics;
import cn.myperf4j.base.metric.exporter.MetricsExporterFactory;
import cn.myperf4j.core.BaseTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/19
 */
public class JvmMetricsSchedulerTest extends BaseTest {

    @Before
    public void setUp() {
        initProperties();
        ProfilingConfig.metricsConfig(MetricsConfig.loadMetricsConfig());

        MetricsConfig metricsConfig = ProfilingConfig.metricsConfig();
        metricsConfig.logRollingTimeUnit("DAILY");
        metricsConfig.logReserveCount(7);
    }

    @Test
    public void test() {
        final String exporter = Metrics.EXPORTER_LOG_STDOUT;
        final JvmMetricsScheduler scheduler = new JvmMetricsScheduler(
                MetricsExporterFactory.getClassMetricsExporter(exporter),
                MetricsExporterFactory.getGcMetricsExporter(exporter),
                MetricsExporterFactory.getGcMetricsV3Exporter(exporter),
                MetricsExporterFactory.getMemoryMetricsExporter(exporter),
                MetricsExporterFactory.getMemoryMetricsV3Exporter(exporter),
                MetricsExporterFactory.getBufferPoolMetricsExporter(exporter),
                MetricsExporterFactory.getThreadMetricsExporter(exporter),
                MetricsExporterFactory.getCompilationExporter(exporter),
                MetricsExporterFactory.getFileDescExporter(exporter)
        );

        final long startMills = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            scheduler.run(startMills, startMills + i * 60 * 1000);
        }
    }
}
