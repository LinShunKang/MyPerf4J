package cn.myperf4j.base.metric.exporter;

import cn.myperf4j.base.metric.exporter.discard.DiscardJvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.exporter.discard.DiscardJvmClassMetricsExporter;
import cn.myperf4j.base.metric.exporter.discard.DiscardJvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.exporter.discard.DiscardJvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.exporter.discard.DiscardJvmGcMetricsExporter;
import cn.myperf4j.base.metric.exporter.discard.DiscardJvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.exporter.discard.DiscardJvmThreadMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpJvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpJvmClassMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpJvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpJvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpJvmGcMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpJvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpJvmThreadMetricsExporter;
import cn.myperf4j.base.metric.exporter.http.influxdb.InfluxHttpMethodMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogJvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogJvmClassMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogJvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogJvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogJvmGcMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogJvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogJvmThreadMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.influxdb.InfluxLogMethodMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogJvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogJvmClassMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogJvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogJvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogJvmGcMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogJvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogJvmThreadMetricsExporter;
import cn.myperf4j.base.metric.exporter.log.standard.StdLogMethodMetricsExporter;

import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_HTTP_INFLUX_DB;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_LOG_INFLUX_DB;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_LOG_STANDARD;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_LOG_STDOUT;

public final class MetricsExporterFactory {

    private MetricsExporterFactory() {
        //empty
    }

    public static JvmClassMetricsExporter getClassMetricsExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_STANDARD:
            case EXPORTER_LOG_STDOUT:
                return new StdLogJvmClassMetricsExporter();
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogJvmClassMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpJvmClassMetricsExporter();
            default:
                return new DiscardJvmClassMetricsExporter();
        }
    }

    public static JvmGcMetricsExporter getGcMetricsExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_STANDARD:
            case EXPORTER_LOG_STDOUT:
                return new StdLogJvmGcMetricsExporter();
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogJvmGcMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpJvmGcMetricsExporter();
            default:
                return new DiscardJvmGcMetricsExporter();
        }
    }

    public static JvmMemoryMetricsExporter getMemoryMetricsExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_STANDARD:
            case EXPORTER_LOG_STDOUT:
                return new StdLogJvmMemoryMetricsExporter();
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogJvmMemoryMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpJvmMemoryMetricsExporter();
            default:
                return new DiscardJvmMemoryMetricsExporter();
        }
    }

    public static JvmBufferPoolMetricsExporter getBufferPoolMetricsExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_STANDARD:
            case EXPORTER_LOG_STDOUT:
                return new StdLogJvmBufferPoolMetricsExporter();
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogJvmBufferPoolMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpJvmBufferPoolMetricsExporter();
            default:
                return new DiscardJvmBufferPoolMetricsExporter();
        }
    }

    public static JvmThreadMetricsExporter getThreadMetricsExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_STANDARD:
            case EXPORTER_LOG_STDOUT:
                return new StdLogJvmThreadMetricsExporter();
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogJvmThreadMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpJvmThreadMetricsExporter();
            default:
                return new DiscardJvmThreadMetricsExporter();
        }
    }

    public static MethodMetricsExporter getMethodMetricsExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogMethodMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpMethodMetricsExporter();
            default:
                return new StdLogMethodMetricsExporter();
        }
    }

    public static JvmFileDescMetricsExporter getFileDescExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_STANDARD:
            case EXPORTER_LOG_STDOUT:
                return new StdLogJvmFileDescMetricsExporter();
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogJvmFileDescMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpJvmFileDescMetricsExporter();
            default:
                return new DiscardJvmFileDescMetricsExporter();
        }
    }

    public static JvmCompilationMetricsExporter getCompilationExporter(String exporter) {
        switch (exporter) {
            case EXPORTER_LOG_STANDARD:
            case EXPORTER_LOG_STDOUT:
                return new StdLogJvmCompilationMetricsExporter();
            case EXPORTER_LOG_INFLUX_DB:
                return new InfluxLogJvmCompilationMetricsExporter();
            case EXPORTER_HTTP_INFLUX_DB:
                return new InfluxHttpJvmCompilationMetricsExporter();
            default:
                return new DiscardJvmCompilationMetricsExporter();
        }
    }
}
