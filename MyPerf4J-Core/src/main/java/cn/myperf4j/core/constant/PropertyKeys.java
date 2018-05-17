package cn.myperf4j.core.constant;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyKeys {

    String PRO_FILE_NAME = "MyPerf4JPropFile";

    String PERF_STATS_PROCESSOR = "MyPerf4J.PSP";

    String RECORDER_MODE = "MyPerf4J.RecMode";

    String MILL_TIME_SLICE = "MyPerf4J.MillTimeSlice";

    String FILTER_INCLUDE_PACKAGES = "MyPerf4J.IncludePackages";

    String FILTER_EXCLUDE_PACKAGES = "MyPerf4J.ExcludePackages";

    String DEBUG_PRINT_DEBUG_LOG = "MyPerf4J.Debug.PrintDebugLog";


    //--------------------------MyPerf4J-ASM独有的属性--------------------------

    String ASM_PROFILING_TYPE = "MyPerf4J.ASM.ProfilingType";

    String ASM_FILTER_EXCLUDE_METHODS = "MyPerf4J.ExcludeMethods";

    String ASM_EXCLUDE_PRIVATE_METHODS = "MyPerf4J.ExcludePrivateMethod";

    String ASM_FILTER_INCLUDE_CLASS_LOADERS = "MyPerf4J.ASM.ExcludeClassLoaders";


}
