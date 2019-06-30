package cn.myperf4j.base.metric.processor;

import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.metric.processor.discard.*;
import cn.myperf4j.base.metric.processor.influx.*;
import cn.myperf4j.base.metric.processor.log.*;

public class MetricsProcessorFactory {

    public static JvmClassMetricsProcessor getClassMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmClassMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxJvmClassMetricsProcessor();
            default:
                return new DiscardJvmClassMetricsProcessor();
        }
    }

    public static JvmGcMetricsProcessor getGcMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmGcMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxJvmGcMetricsProcessor();
            default:
                return new DiscardJvmGcMetricsProcessor();
        }
    }

    public static JvmMemoryMetricsProcessor getMemoryMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmMemoryMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxJvmMemoryMetricsProcessor();
            default:
                return new DiscardJvmMemoryMetricsProcessor();
        }
    }

    public static JvmBufferPoolMetricsProcessor getBufferPoolMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmBufferPoolMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxJvmBufferPoolMetricsProcessor();
            default:
                return new DiscardJvmBufferPoolMetricsProcessor();
        }
    }

    public static JvmThreadMetricsProcessor getThreadMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmThreadMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxJvmThreadMetricsProcessor();
            default:
                return new DiscardJvmThreadMetricsProcessor();
        }
    }

    public static MethodMetricsProcessor getMethodMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerMethodMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxMethodMetricsProcessor();
            default:
                return new LoggerMethodMetricsProcessor();
        }
    }

}
