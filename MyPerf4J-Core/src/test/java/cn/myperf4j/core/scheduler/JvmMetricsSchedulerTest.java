package cn.myperf4j.core.scheduler;

import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.constant.PropertyValues.Metrics;
import cn.myperf4j.base.metric.exporter.JvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmClassMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsV3Exporter;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmThreadMetricsExporter;
import cn.myperf4j.base.metric.exporter.MetricsExporterFactory;
import cn.myperf4j.core.BaseTest;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/19
 */
public class JvmMetricsSchedulerTest extends BaseTest {

    @Test
    public void test() {
        init();

        String exporter = Metrics.EXPORTER_LOG_STDOUT;
        JvmClassMetricsExporter classExporter = MetricsExporterFactory.getClassMetricsExporter(exporter);
        JvmGcMetricsExporter gcExporter = MetricsExporterFactory.getGcMetricsExporter(exporter);
        JvmGcMetricsV3Exporter gcExporterV2 = MetricsExporterFactory.getGcMetricsV3Exporter(exporter);
        JvmMemoryMetricsExporter memoryExporter = MetricsExporterFactory.getMemoryMetricsExporter(exporter);
        JvmBufferPoolMetricsExporter bufferPoolExporter = MetricsExporterFactory.getBufferPoolMetricsExporter(exporter);
        JvmThreadMetricsExporter threadExporter = MetricsExporterFactory.getThreadMetricsExporter(exporter);
        JvmCompilationMetricsExporter compilationExporter = MetricsExporterFactory.getCompilationExporter(exporter);
        JvmFileDescMetricsExporter fileDescExporter = MetricsExporterFactory.getFileDescExporter(exporter);
        JvmMetricsScheduler scheduler = new JvmMetricsScheduler(
                classExporter,
                gcExporter,
                gcExporterV2,
                memoryExporter,
                bufferPoolExporter,
                threadExporter,
                compilationExporter,
                fileDescExporter
        );

        long startMills = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            scheduler.run(startMills, startMills + i * 60 * 1000);
        }
    }

    private void init() {
        initProperties();
        ProfilingConfig.metricsConfig(MetricsConfig.loadMetricsConfig());

        MetricsConfig metricsConfig = ProfilingConfig.metricsConfig();
        metricsConfig.logRollingTimeUnit("DAILY");
        metricsConfig.logReserveCount(7);
    }
}
