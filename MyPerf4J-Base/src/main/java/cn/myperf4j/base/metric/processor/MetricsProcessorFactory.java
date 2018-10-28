package cn.myperf4j.base.metric.processor;

import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmClassMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmGCMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmMemoryMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmThreadMetricsProcessor;
import cn.myperf4j.base.metric.processor.influxdb.*;
import cn.myperf4j.base.metric.processor.log.*;

public class MetricsProcessorFactory {

    public static JvmClassMetricsProcessor getClassMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmClassMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxDBJvmClassMetricsProcessor();
            default:
                return new DiscardJvmClassMetricsProcessor();
        }
    }

    public static JvmGCMetricsProcessor getGCMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmGCMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxDBJvmGCMetricsProcessor();
            default:
                return new DiscardJvmGCMetricsProcessor();
        }
    }

    public static JvmMemoryMetricsProcessor getMemoryMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmMemoryMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxDBJvmMemoryMetricsProcessor();
            default:
                return new DiscardJvmMemoryMetricsProcessor();
        }
    }

    public static JvmThreadMetricsProcessor getThreadMetricsProcessor(int processorType) {
        switch (processorType) {
            case PropertyValues.METRICS_PROCESS_TYPE_STDOUT:
            case PropertyValues.METRICS_PROCESS_TYPE_LOGGER:
                return new LoggerJvmThreadMetricsProcessor();
            case PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxDBJvmThreadMetricsProcessor();
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
                return new InfluxDBMethodMetricsProcessor();
            default:
                return new LoggerMethodMetricsProcessor();
        }
    }

}
