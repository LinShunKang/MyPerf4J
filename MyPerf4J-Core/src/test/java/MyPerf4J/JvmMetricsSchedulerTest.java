package MyPerf4J;

import cn.myperf4j.base.Scheduler;
import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.metric.processor.*;
import cn.myperf4j.core.scheduler.JvmMetricsScheduler;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/19
 */
public class JvmMetricsSchedulerTest {

    @Test
    public void test() {
        init();

        int processorType = PropertyValues.METRICS_PROCESS_TYPE_STDOUT;
        JvmClassMetricsProcessor classProcessor = MetricsProcessorFactory.getClassMetricsProcessor(processorType);
        JvmGCMetricsProcessor gcProcessor = MetricsProcessorFactory.getGCMetricsProcessor(processorType);
        JvmMemoryMetricsProcessor memoryProcessor = MetricsProcessorFactory.getMemoryMetricsProcessor(processorType);
        JvmThreadMetricsProcessor threadProcessor = MetricsProcessorFactory.getThreadMetricsProcessor(processorType);
        Scheduler scheduler = new JvmMetricsScheduler(classProcessor, gcProcessor, memoryProcessor, threadProcessor);
        long startMills = System.currentTimeMillis();
        scheduler.run(startMills, startMills + 60 * 1000);
    }

    private void init() {
        ProfilingConfig config = ProfilingConfig.getInstance();
        config.setMethodMetricsFile("/tmp/metrics.log");
        config.setClassMetricsFile("/tmp/metrics.log");
        config.setGcMetricsFile("/tmp/metrics.log");
        config.setMemoryMetricsFile("/tmp/metrics.log");
        config.setThreadMetricsFile("/tmp/metrics.log");
        config.setLogRollingTimeUnit("DAILY");
    }
}
