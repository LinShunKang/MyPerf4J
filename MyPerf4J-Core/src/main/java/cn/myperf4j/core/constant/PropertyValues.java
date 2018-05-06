package cn.myperf4j.core.constant;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyValues {

    String RECORDER_MODE_ACCURATE = "accurate";

    long DEFAULT_TIME_SLICE = 60 * 1000L;

    long MIN_TIME_SLICE = 10 * 1000L;

    long MAX_TIME_SLICE = 10 * 60 * 1000L;

    String FILTER_PACKAGES_SPLIT = ";";

    String ASM_PROFILING_TYPE_PROFILER = "byProfiler";

    String ASM_PROFILING_TYPE_PACKAGE = "byPackage";

}
