package cn.myperf4j.base.metric.processor;

import cn.myperf4j.base.metric.processor.discard.DiscardJvmBufferPoolMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmClassMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmCompilationProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmFileDescProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmGcMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmMemoryMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmThreadMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogJvmBufferPoolMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogJvmClassMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogJvmCompilationMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogJvmFileDescMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogJvmGcMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogJvmMemoryMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogJvmThreadMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.influxdb.InfluxLogMethodMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogJvmBufferPoolMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogJvmClassMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogJvmCompilationMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogJvmFileDescMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogJvmGcMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogJvmMemoryMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogJvmThreadMetricsProcessor;
import cn.myperf4j.base.metric.processor.log.standard.StdLogMethodMetricsProcessor;

import static cn.myperf4j.base.constant.PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB;
import static cn.myperf4j.base.constant.PropertyValues.METRICS_PROCESS_TYPE_LOGGER;
import static cn.myperf4j.base.constant.PropertyValues.METRICS_PROCESS_TYPE_STDOUT;

public class MetricsProcessorFactory {

    public static JvmClassMetricsProcessor getClassMetricsProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmClassMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmClassMetricsProcessor();
            default:
                return new DiscardJvmClassMetricsProcessor();
        }
    }

    public static JvmGcMetricsProcessor getGcMetricsProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmGcMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmGcMetricsProcessor();
            default:
                return new DiscardJvmGcMetricsProcessor();
        }
    }

    public static JvmMemoryMetricsProcessor getMemoryMetricsProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmMemoryMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmMemoryMetricsProcessor();
            default:
                return new DiscardJvmMemoryMetricsProcessor();
        }
    }

    public static JvmBufferPoolMetricsProcessor getBufferPoolMetricsProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmBufferPoolMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmBufferPoolMetricsProcessor();
            default:
                return new DiscardJvmBufferPoolMetricsProcessor();
        }
    }

    public static JvmThreadMetricsProcessor getThreadMetricsProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmThreadMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmThreadMetricsProcessor();
            default:
                return new DiscardJvmThreadMetricsProcessor();
        }
    }

    public static MethodMetricsProcessor getMethodMetricsProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogMethodMetricsProcessor();
            default:
                return new StdLogMethodMetricsProcessor();
        }
    }

    public static JvmFileDescProcessor getFileDescProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmFileDescMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmFileDescMetricsProcessor();
            default:
                return new DiscardJvmFileDescProcessor();
        }
    }

    public static JvmCompilationProcessor getCompilationProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmCompilationMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmCompilationMetricsProcessor();
            default:
                return new DiscardJvmCompilationProcessor();
        }
    }

}
