package cn.myperf4j.core.scheduler;

import cn.myperf4j.base.Scheduler;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;
import cn.myperf4j.base.metric.JvmClassMetrics;
import cn.myperf4j.base.metric.JvmCompilationMetrics;
import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.JvmGcMetrics;
import cn.myperf4j.base.metric.JvmGcMetricsV3;
import cn.myperf4j.base.metric.JvmMemoryMetrics;
import cn.myperf4j.base.metric.JvmMemoryMetricsV3;
import cn.myperf4j.base.metric.JvmThreadMetrics;
import cn.myperf4j.base.metric.collector.JvmBufferPoolCollector;
import cn.myperf4j.base.metric.collector.JvmClassCollector;
import cn.myperf4j.base.metric.collector.JvmCompilationCollector;
import cn.myperf4j.base.metric.collector.JvmFileDescCollector;
import cn.myperf4j.base.metric.collector.JvmGcCollector;
import cn.myperf4j.base.metric.collector.JvmGcV3Collector;
import cn.myperf4j.base.metric.collector.JvmMemoryCollector;
import cn.myperf4j.base.metric.collector.JvmMemoryV3Collector;
import cn.myperf4j.base.metric.collector.JvmThreadCollector;
import cn.myperf4j.base.metric.exporter.JvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmClassMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsV3Exporter;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsV3Exporter;
import cn.myperf4j.base.metric.exporter.JvmThreadMetricsExporter;
import cn.myperf4j.base.util.Logger;

import java.util.List;

/**
 * Created by LinShunkang on 2018/8/22
 */
public class JvmMetricsScheduler implements Scheduler {

    private final JvmClassMetricsExporter classMetricsProcessor;

    private final JvmGcMetricsExporter gcMetricsProcessor;

    private final JvmGcMetricsV3Exporter gcMetricsV3Processor;

    private final JvmMemoryMetricsExporter memoryMetricsProcessor;

    private final JvmMemoryMetricsV3Exporter memoryMetricsV3Processor;

    private final JvmBufferPoolMetricsExporter bufferPoolMetricsProcessor;

    private final JvmThreadMetricsExporter threadMetricsProcessor;

    private final JvmCompilationMetricsExporter compilationProcessor;

    private final JvmFileDescMetricsExporter fileDescProcessor;

    public JvmMetricsScheduler(JvmClassMetricsExporter classMetricsProcessor,
                               JvmGcMetricsExporter gcMetricsProcessor,
                               JvmGcMetricsV3Exporter gcMetricsV3Processor,
                               JvmMemoryMetricsExporter memoryMetricsProcessor,
                               JvmMemoryMetricsV3Exporter memoryMetricsV3Processor,
                               JvmBufferPoolMetricsExporter bufferPoolMetricsProcessor,
                               JvmThreadMetricsExporter threadMetricsProcessor,
                               JvmCompilationMetricsExporter compilationProcessor,
                               JvmFileDescMetricsExporter fileDescProcessor) {
        this.classMetricsProcessor = classMetricsProcessor;
        this.gcMetricsProcessor = gcMetricsProcessor;
        this.gcMetricsV3Processor = gcMetricsV3Processor;
        this.memoryMetricsProcessor = memoryMetricsProcessor;
        this.memoryMetricsV3Processor = memoryMetricsV3Processor;
        this.bufferPoolMetricsProcessor = bufferPoolMetricsProcessor;
        this.threadMetricsProcessor = threadMetricsProcessor;
        this.compilationProcessor = compilationProcessor;
        this.fileDescProcessor = fileDescProcessor;
    }

    @Override
    public void run(long lastTimeSliceStartTime, long millTimeSlice) {
        final long stopMillis = lastTimeSliceStartTime + millTimeSlice;
        processClassMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processGCMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processGCMetricsV3(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processMemoryMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processMemoryMetricsV3(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processBufferPoolMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processThreadMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processCompilationMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
        processFileDescMetrics(lastTimeSliceStartTime, lastTimeSliceStartTime, stopMillis);
    }

    @Override
    public String name() {
        return "JvmMetricsCollector";
    }

    private void processClassMetrics(long processId, long startMillis, long stopMillis) {
        classMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            final JvmClassMetrics metrics = JvmClassCollector.collectClassMetrics();
            classMetricsProcessor.process(metrics, processId, startMillis, stopMillis);
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processClassMetrics(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            classMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processGCMetrics(long processId, long startMillis, long stopMillis) {
        gcMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            final JvmGcMetrics metrics = JvmGcCollector.collectGcMetrics();
            gcMetricsProcessor.process(metrics, processId, startMillis, stopMillis);
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processGCMetrics(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            gcMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processGCMetricsV3(long processId, long startMillis, long stopMillis) {
        gcMetricsV3Processor.beforeProcess(processId, startMillis, stopMillis);
        try {
            for (JvmGcMetricsV3 metrics : JvmGcV3Collector.collectGcMetrics()) {
                gcMetricsV3Processor.process(metrics, processId, startMillis, stopMillis);
            }
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processGCMetricsV3(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            gcMetricsV3Processor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processMemoryMetrics(long processId, long startMillis, long stopMillis) {
        memoryMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            final JvmMemoryMetrics metrics = JvmMemoryCollector.collectMemoryMetrics();
            memoryMetricsProcessor.process(metrics, processId, startMillis, stopMillis);
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processMemoryMetrics(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            memoryMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processMemoryMetricsV3(long processId, long startMillis, long stopMillis) {
        memoryMetricsV3Processor.beforeProcess(processId, startMillis, stopMillis);
        try {
            for (JvmMemoryMetricsV3 metrics : JvmMemoryV3Collector.collectMemoryMetrics()) {
                memoryMetricsV3Processor.process(metrics, processId, startMillis, stopMillis);
            }
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processMemoryMetricsV3(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            memoryMetricsV3Processor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processBufferPoolMetrics(long processId, long startMillis, long stopMillis) {
        bufferPoolMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            for (JvmBufferPoolMetrics metrics : JvmBufferPoolCollector.collectBufferPoolMetrics()) {
                bufferPoolMetricsProcessor.process(metrics, processId, startMillis, stopMillis);
            }
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processBufferPoolMetrics(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            bufferPoolMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processThreadMetrics(long processId, long startMillis, long stopMillis) {
        threadMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            final JvmThreadMetrics metrics = JvmThreadCollector.collectThreadMetrics();
            threadMetricsProcessor.process(metrics, processId, startMillis, stopMillis);
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processThreadMetrics(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            threadMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processCompilationMetrics(long processId, long startMillis, long stopMillis) {
        compilationProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            final JvmCompilationMetrics metrics = JvmCompilationCollector.collectCompilationMetrics();
            compilationProcessor.process(metrics, processId, startMillis, stopMillis);
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processCompilationMetrics(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            compilationProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processFileDescMetrics(long processId, long startMillis, long stopMillis) {
        fileDescProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            final JvmFileDescriptorMetrics metrics = JvmFileDescCollector.collectFileDescMetrics();
            fileDescProcessor.process(metrics, processId, startMillis, stopMillis);
        } catch (Throwable t) {
            Logger.error("JvmMetricsScheduler.processFileDescMetrics(" + processId + ", " + startMillis + ", "
                    + stopMillis + ")", t);
        } finally {
            fileDescProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }
}
