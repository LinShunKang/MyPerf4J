package cn.myperf4j.base.constant;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyKeys {

    String PRO_FILE_NAME = "MyPerf4JPropFile";

    String APP_NAME = "AppName";

    String METRICS_PROCESS_TYPE = "MetricsProcessorType";

    String METHOD_METRICS_FILE = "MethodMetricsFile";

    String CLASS_METRICS_FILE = "ClassMetricsFile";

    String GC_METRICS_FILE = "GCMetricsFile";

    String MEM_METRICS_FILE = "MemMetricsFile";

    String BUF_POOL_METRICS_FILE = "BufPoolMetricsFile";

    String THREAD_METRICS_FILE = "ThreadMetricsFile";

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

}
