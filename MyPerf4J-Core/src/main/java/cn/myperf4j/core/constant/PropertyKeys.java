package cn.myperf4j.core.constant;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyKeys {

    String PRO_FILE_NAME = "MyPerf4JPropFile";

    String PERF_STATS_PROCESSOR = "PerfStatsProcessor";

    String RECORDER_MODE = "RecorderMode";

    String MILL_TIME_SLICE = "MillTimeSlice";

    String FILTER_INCLUDE_PACKAGES = "IncludePackages";

    String FILTER_EXCLUDE_PACKAGES = "ExcludePackages";

    String DEBUG_PRINT_DEBUG_LOG = "Debug.PrintDebugLog";


    //--------------------------MyPerf4J-ASM独有的属性--------------------------

    String ASM_PROFILING_TYPE = "ASM.ProfilingType";

    String ASM_FILTER_EXCLUDE_METHODS = "ASM.ExcludeMethods";

    String ASM_EXCLUDE_PRIVATE_METHODS = "ASM.ExcludePrivateMethod";

    String ASM_FILTER_INCLUDE_CLASS_LOADERS = "ASM.ExcludeClassLoaders";


}
