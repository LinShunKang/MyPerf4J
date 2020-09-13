package cn.myperf4j.base.constant;

import cn.myperf4j.base.config.ConfigKey;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyKeys {

    String PRO_FILE_NAME = "MyPerf4JPropFile";

    interface Basic {

        ConfigKey APP_NAME = ConfigKey.of("app_name", "AppName");

        ConfigKey DEBUG = ConfigKey.of("debug", "Debug.PrintDebugLog");

        ConfigKey PROPERTIES_FILE_DIR = ConfigKey.of("properties.dir", "MyPerf4JPropDIR");
    }

    interface HttpServer {

        ConfigKey PORT = ConfigKey.of("http.server.port", "http.server.port");

        ConfigKey MIN_WORKERS = ConfigKey.of("http.server.min_workers", "http.server.min_workers");

        ConfigKey MAX_WORKERS = ConfigKey.of("http.server.max_workers", "http.server.max_workers");

        ConfigKey ACCEPT_COUNT = ConfigKey.of("http.server.accept_count", "http.server.accept_count");
    }

    interface Metrics {

        ConfigKey EXPORTER = ConfigKey.of("metrics.exporter", "MetricsProcessorType");

        ConfigKey TIME_SLICE_METHOD = ConfigKey.of("metrics.time_slice.method", "MethodMilliTimeSlice");

        ConfigKey TIME_SLICE_JVM = ConfigKey.of("metrics.time_slice.jvm", "JvmMilliTimeSlice");

        ConfigKey METHOD_SHOW_PARAMS = ConfigKey.of("metrics.method.show_params", "ShowMethodParams");

        ConfigKey CLASS_LEVEL_MAPPINGS = ConfigKey.of("metrics.method.class_level_mappings", "ClassLevelMapping");

        ConfigKey LOG_METHOD = ConfigKey.of("metrics.log.method", "MethodMetricsFile");

        ConfigKey LOG_CLASS_LOADING = ConfigKey.of("metrics.log.class_loading", "ClassMetricsFile");

        ConfigKey LOG_GC = ConfigKey.of("metrics.log.gc", "GCMetricsFile");

        ConfigKey LOG_MEMORY = ConfigKey.of("metrics.log.memory", "MemMetricsFile");

        ConfigKey LOG_BUFF_POOL = ConfigKey.of("metrics.log.buff_pool", "BufPoolMetricsFile");

        ConfigKey LOG_THREAD = ConfigKey.of("metrics.log.thread", "ThreadMetricsFile");

        ConfigKey LOG_FILE_DESC = ConfigKey.of("metrics.log.file_desc", "FileDescMetricsFile");

        ConfigKey LOG_COMPILATION = ConfigKey.of("metrics.log.compilation", "CompilationMetricsFile");

        ConfigKey LOG_ROLLING_TIME_UNIT = ConfigKey.of("metrics.log.rolling.time_unit", "LogRollingTimeUnit");

        ConfigKey LOG_RESERVE_COUNT = ConfigKey.of("metrics.log.reserve.count", "LogReserveCount");
    }

    interface InfluxDB {

        ConfigKey HOST = ConfigKey.of("influxdb.host", "influxdb.host");

        ConfigKey PORT = ConfigKey.of("influxdb.port", "influxdb.port");

        ConfigKey DATABASE = ConfigKey.of("influxdb.database", "influxdb.database");

        ConfigKey USERNAME = ConfigKey.of("influxdb.username", "influxdb.username");

        ConfigKey PASSWORD = ConfigKey.of("influxdb.password", "influxdb.password");

        ConfigKey CONN_TIMEOUT = ConfigKey.of("influxdb.conn_timeout", "influxdb.conn_timeout");

        ConfigKey READ_TIMEOUT = ConfigKey.of("influxdb.read_timeout", "influxdb.read_timeout");
    }

    interface Filter {

        ConfigKey PACKAGES_INCLUDE = ConfigKey.of("filter.packages.include", "IncludePackages");

        ConfigKey PACKAGES_EXCLUDE = ConfigKey.of("filter.packages.exclude", "ExcludePackages");

        ConfigKey METHODS_EXCLUDE = ConfigKey.of("filter.methods.exclude", "ExcludeMethods");

        ConfigKey METHODS_EXCLUDE_PRIVATE = ConfigKey.of("filter.methods.exclude_private", "ExcludePrivateMethod");

        ConfigKey CLASS_LOADERS_EXCLUDE = ConfigKey.of("filter.class_loaders.exclude", "ExcludeClassLoaders");
    }

    interface Recorder {

        ConfigKey BACKUP_COUNT = ConfigKey.of("recorders.backup_count", "BackupRecordersCount");

        ConfigKey MODE = ConfigKey.of("recorder.mode", "RecorderMode");

        ConfigKey SIZE_TIMING_ARR = ConfigKey.of("recorder.size.timing_arr", "ProfilingTimeThreshold");

        ConfigKey SIZE_TIMING_MAP = ConfigKey.of("recorder.size.timing_map", "ProfilingOutThresholdCount");
    }
}
