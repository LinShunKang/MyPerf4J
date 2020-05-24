package cn.myperf4j.base.constant;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyKeys {

    String PRO_FILE_NAME = "MyPerf4JPropFile";

    String PRO_FILE_DIR = "MyPerf4JPropDIR";

    String APP_NAME = "app_name";

    String DEBUG = "debug";

    String METRICS_PROCESS_TYPE = "MetricsProcessorType";

    String METHOD_METRICS_FILE = "MethodMetricsFile";

    String CLASS_METRICS_FILE = "ClassMetricsFile";

    String GC_METRICS_FILE = "GCMetricsFile";

    String MEM_METRICS_FILE = "MemMetricsFile";

    String BUF_POOL_METRICS_FILE = "BufPoolMetricsFile";

    String THREAD_METRICS_FILE = "ThreadMetricsFile";

    String FILE_DESC_METRICS_FILE = "FileDescMetricsFile";

    String COMPILATION_METRICS_FILE = "CompilationMetricsFile";

    String LOG_ROLLING_TIME_TIME_UNIT = "LogRollingTimeUnit";

    String LOG_RESERVE_COUNT = "LogReserveCount";

    String RECORDER_MODE = "RecorderMode";

    String BACKUP_RECORDERS_COUNT = "BackupRecordersCount";

    @Deprecated
    String MILLI_TIME_SLICE = "MilliTimeSlice";

    String METHOD_MILLI_TIME_SLICE = "MethodMilliTimeSlice";

    String JVM_MILLI_TIME_SLICE = "JvmMilliTimeSlice";

    String SHOW_METHOD_PARAMS = "ShowMethodParams";

    String CLASS_LEVEL_MAPPING = "ClassLevelMapping";

    String FILTER_INCLUDE_PACKAGES = "IncludePackages";

    String FILTER_EXCLUDE_PACKAGES = "ExcludePackages";

    String DEBUG_PRINT_DEBUG_LOG = "Debug.PrintDebugLog";

    String FILTER_EXCLUDE_METHODS = "ExcludeMethods";

    String EXCLUDE_PRIVATE_METHODS = "ExcludePrivateMethod";

    String FILTER_INCLUDE_CLASS_LOADERS = "ExcludeClassLoaders";

    String PROFILING_PARAMS_FILE_NAME = "ProfilingParamsFile";

    String PROFILING_TIME_THRESHOLD = "ProfilingTimeThreshold";

    String PROFILING_OUT_THRESHOLD_COUNT = "ProfilingOutThresholdCount";


    interface Metrics {

        String EXPORTER = "metrics.exporter";

        String TIME_SLICE_METHOD = "metrics.time_slice.method";

        String TIME_SLICE_JVM = "metrics.time_slice.jvm";

        String SHOW_PARAMS = "metrics.method.show_params";

        String CLASS_LEVEL_MAPPING = "metrics.method.class_level_mapping";

        String LOG_METHOD = "metrics.log.method";

        String LOG_CLASS_LOADING = "metrics.log.class_loading";

        String LOG_GC = "metrics.log.gc";

        String LOG_MEMORY = "metrics.log.memory";

        String LOG_BUFF_POOL = "metrics.log.buff_pool";

        String LOG_THREAD = "metrics.log.thread";

        String LOG_FILE_DESC = "metrics.log.file_desc";

        String LOG_COMPILATION = "metrics.log.compilation";

    }

    interface InfluxDB {

        String HOST = "influxdb.host";

        String PORT = "influxdb.port";

        String DATABASE = "influxdb.database";

        String USERNAME = "influxdb.username";

        String PASSWORD = "influxdb.password";

        String CONN_TIMEOUT = "influxdb.conn_timeout";

        String READ_TIMEOUT = "influxdb.READ_TIMEOUT";

    }

    interface Filter {

        String PACKAGES_INCLUDE = "filter.packages.include";

        String PACKAGES_EXCLUDE = "filter.packages.exclude";

        String METHODS_EXCLUDE = "filter.methods.exclude";

        String METHODS_EXCLUDE_PRIVATE = "filter.methods.exclude_private";

        String CLASS_LOADERS_EXCLUDE = "filter.class_loaders.exclude";

    }

    interface Recorder {

        String MODE = "recorder.mode";

        String BACKUP_COUNT = "recorder.backup_count";

        String SIZE_TIMING_ARR = "recorder.size.timing_arr";

        String SIZE_TIMING_MAP = "recorder.size.timing_map";

        String PARAMS_FILE = "recorder.params_file";

    }
}
