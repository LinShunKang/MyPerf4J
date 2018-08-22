package cn.myperf4j.core.scheduler;


import cn.myperf4j.base.metric.JVMClassMetrics;
import cn.myperf4j.base.metric.JVMGCMetrics;
import cn.myperf4j.base.metric.JVMMemoryMetrics;
import cn.myperf4j.base.metric.JVMThreadMetrics;
import cn.myperf4j.base.metric.processor.*;

import java.lang.management.*;
import java.util.List;

/**
 * Created by LinShunkang on 2018/8/22
 */
public class JVMMetricsScheduler implements Scheduler {

    private JVMClassMetricsProcessor classMetricsProcessor;

    private JVMGCMetricsProcessor gcMetricsProcessor;

    private JVMMemoryMetricsProcessor memoryMetricsProcessor;

    private JVMThreadMetricsProcessor threadMetricsProcessor;


    public JVMMetricsScheduler(JVMClassMetricsProcessor classMetricsProcessor,
                               JVMGCMetricsProcessor gcMetricsProcessor,
                               JVMMemoryMetricsProcessor memoryMetricsProcessor,
                               JVMThreadMetricsProcessor threadMetricsProcessor) {
        this.classMetricsProcessor = classMetricsProcessor;
        this.gcMetricsProcessor = gcMetricsProcessor;
        this.memoryMetricsProcessor = memoryMetricsProcessor;
        this.threadMetricsProcessor = threadMetricsProcessor;
    }

    @Override
    public void run(long currentMills, long millTimeSlice, long nextTimeSliceEndTime) {
        long processId = currentMills;
        long startMillis = currentMills - millTimeSlice;
        long stopMillis = currentMills;

        processJVMClassMetrics(processId, startMillis, stopMillis);
        processJVMGCMetrics(processId, startMillis, stopMillis);
        processJVMMemoryMetrics(processId, startMillis, stopMillis);
        processJVMThreadMetrics(processId, startMillis, stopMillis);

    }

    private void processJVMClassMetrics(long processId, long startMillis, long stopMillis) {
        JVMClassMetrics classMetrics = new JVMClassMetrics(ManagementFactory.getClassLoadingMXBean());

        classMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            classMetricsProcessor.process(classMetrics, processId, startMillis, stopMillis);
        } finally {
            classMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processJVMGCMetrics(long processId, long startMillis, long stopMillis) {
        gcMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
            for (GarbageCollectorMXBean bean : garbageCollectorMxBeans) {
                JVMGCMetrics gcMetrics = new JVMGCMetrics(bean);
                gcMetricsProcessor.process(gcMetrics, processId, startMillis, stopMillis);
            }
        } finally {
            gcMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processJVMMemoryMetrics(long processId, long startMillis, long stopMillis) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMem = memoryMXBean.getNonHeapMemoryUsage();
        MemoryUsage heapMem = memoryMXBean.getHeapMemoryUsage();

        memoryMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            memoryMetricsProcessor.process(new JVMMemoryMetrics(nonHeapMem, heapMem), processId, startMillis, stopMillis);
        } finally {
            memoryMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

    private void processJVMThreadMetrics(long processId, long startMillis, long stopMillis) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        threadMetricsProcessor.beforeProcess(processId, startMillis, stopMillis);
        try {
            threadMetricsProcessor.process(new JVMThreadMetrics(threadMXBean), processId, startMillis, stopMillis);
        } finally {
            threadMetricsProcessor.afterProcess(processId, startMillis, stopMillis);
        }
    }

}
