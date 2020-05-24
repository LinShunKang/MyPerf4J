package cn.myperf4j.base.metric.processor;

import cn.myperf4j.base.metric.processor.discard.DiscardJvmBufferPoolMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmClassMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmCompilationMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmFileDescMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmGcMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmMemoryMetricsProcessor;
import cn.myperf4j.base.metric.processor.discard.DiscardJvmThreadMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpJvmBufferPoolMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpJvmClassMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpJvmCompilationMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpJvmFileDescMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpJvmGcMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpJvmMemoryMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpJvmThreadMetricsProcessor;
import cn.myperf4j.base.metric.processor.http.influxdb.InfluxHttpMethodMetricsProcessor;
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
import static cn.myperf4j.base.constant.PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB_HTTP;
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
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpJvmClassMetricsProcessor();
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
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpJvmGcMetricsProcessor();
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
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpJvmMemoryMetricsProcessor();
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
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpJvmBufferPoolMetricsProcessor();
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
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpJvmThreadMetricsProcessor();
            default:
                return new DiscardJvmThreadMetricsProcessor();
        }
    }

    public static MethodMetricsProcessor getMethodMetricsProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogMethodMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpMethodMetricsProcessor();
            default:
                return new StdLogMethodMetricsProcessor();
        }
    }

    public static JvmFileDescMetricsProcessor getFileDescProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmFileDescMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmFileDescMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpJvmFileDescMetricsProcessor();
            default:
                return new DiscardJvmFileDescMetricsProcessor();
        }
    }

    public static JvmCompilationMetricsProcessor getCompilationProcessor(int processorType) {
        switch (processorType) {
            case METRICS_PROCESS_TYPE_STDOUT:
            case METRICS_PROCESS_TYPE_LOGGER:
                return new StdLogJvmCompilationMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB:
                return new InfluxLogJvmCompilationMetricsProcessor();
            case METRICS_PROCESS_TYPE_INFLUX_DB_HTTP:
                return new InfluxHttpJvmCompilationMetricsProcessor();
            default:
                return new DiscardJvmCompilationMetricsProcessor();
        }
    }

}
