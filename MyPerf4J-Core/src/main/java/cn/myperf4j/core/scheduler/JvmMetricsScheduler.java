package cn.myperf4j.core.scheduler;

import cn.myperf4j.base.Scheduler;
import cn.myperf4j.base.metric.*;
import cn.myperf4j.base.metric.collector.JvmGcCollector;
import cn.myperf4j.base.metric.collector.JvmMemoryCollector;
import cn.myperf4j.base.metric.processor.*;

import java.lang.management.*;
import java.util.List;

/**
 * Created by LinShunkang on 2018/8/22
 */
public class JvmMetricsScheduler implements Scheduler {

    private JvmClassMetricsProcessor classMetricsProcessor;

    private JvmGcMetricsProcessor gcMetricsProcessor;

    private JvmMemoryMetricsProcessor memoryMetricsProcessor;

    private JvmBufferPoolMetricsProcessor bufferPoolMetricsProcessor;

    private JvmThreadMetricsProcessor threadMetricsProcessor;


    public JvmMetricsScheduler(JvmClassMetricsProcessor classMetricsProcessor,
                               JvmGcMetricsProcessor gcMetricsProcessor,
                               JvmMemoryMetricsProcessor memoryMetricsProcessor,
                               JvmBufferPoolMetricsProcessor bufferPoolMetricsProcessor,
                               JvmThreadMetricsProcessor threadMetricsProcessor) {
        this.classMetricsProcessor = classMetricsProcessor;
        this.gcMetricsProcessor = gcMetricsProcessor;
        this.memoryMetricsProcessor = memoryMetricsProcessor;
        this.bufferPoolMetricsProcessor = bufferPoolMetricsProcessor;
        this.threadMetricsProcessor = threadMetricsProcessor;
    }

    @Override
    public void run(long lastTimeSliceStartTime, long millTimeSlice) {
        long stopMillis = lastTimeSliceStartTime + millTimeSlice;

        processJvmClassMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processJvmGCMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processJvmMemoryMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processJvmBufferPoolMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processJvmThreadMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
    }

    private void processJvmClassMetrics(long processId, long startMillis, long stopMillis) {
        classMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            JvmClassMetrics classMetrics = new JvmClassMetrics(ManagementFactory.getClassLoadingMXBean());
            classMetricsProcessor.process(classMetrics, processId, startMillis, stopMillis);
        } finally {
            classMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processJvmGCMetrics(long processId, long startMillis, long stopMillis) {
        gcMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            JvmGcMetrics jvmGcMetrics = JvmGcCollector.getInstance().collectGcMetrics();
            gcMetricsProcessor.process(jvmGcMetrics, processId, startMillis, stopMillis);
        } finally {
            gcMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processJvmMemoryMetrics(long processId, long startMillis, long stopMillis) {
        memoryMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            JvmMemoryMetrics jvmMemoryMetrics = JvmMemoryCollector.collectMemoryMetrics();
            memoryMetricsProcessor.process(jvmMemoryMetrics, processId, startMillis, stopMillis);
        } finally {
            memoryMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processJvmBufferPoolMetrics(long processId, long startMillis, long stopMillis) {
        bufferPoolMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
            for (int i = 0; i < pools.size(); i++) {
                JvmBufferPoolMetrics metrics = new JvmBufferPoolMetrics(pools.get(i));
                bufferPoolMetricsProcessor.process(metrics, processId, startMillis, stopMillis);
            }
        } finally {
            bufferPoolMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processJvmThreadMetrics(long processId, long startMillis, long stopMillis) {
        threadMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            threadMetricsProcessor.process(new JvmThreadMetrics(threadMXBean), processId, startMillis, stopMillis);
        } finally {
            threadMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }
}
