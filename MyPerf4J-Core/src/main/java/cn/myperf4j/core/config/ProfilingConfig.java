package cn.myperf4j.core.config;

import cn.myperf4j.core.constant.PropertyValues;

/**
 * Created by LinShunkang on 2018/5/12
 */
public class ProfilingConfig {

    private static final ProfilingConfig instance = new ProfilingConfig();

    private String perStatsProcessor;

    private String recorderMode;

    private long milliTimeSlice;

    private String includePackages;

    private String excludePackages;

    private String asmExcludeClassLoaders;

    private boolean printDebugLog;

    private String asmProfilingType;

    private String asmExcludeMethods;

    private boolean asmExcludePrivateMethod;

    /**
     * singleton pattern
     */
    public static ProfilingConfig getInstance() {
        return instance;
    }

    public String getPerStatsProcessor() {
        return perStatsProcessor;
    }

    public void setPerStatsProcessor(String perStatsProcessor) {
        this.perStatsProcessor = perStatsProcessor;
    }

    public String getRecorderMode() {
        return recorderMode;
    }

    public boolean isAccurateMode() {
        return recorderMode.equals(PropertyValues.RECORDER_MODE_ACCURATE);
    }

    public void setRecorderMode(String recorderMode) {
        this.recorderMode = recorderMode;
    }

    public long getMilliTimeSlice() {
        return milliTimeSlice;
    }

    public void setMilliTimeSlice(long milliTimeSlice) {
        this.milliTimeSlice = milliTimeSlice;
    }

    public String getIncludePackages() {
        return includePackages;
    }

    public void setIncludePackages(String includePackages) {
        this.includePackages = includePackages;
    }

    public String getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(String excludePackages) {
        this.excludePackages = excludePackages;
    }

    public String getAsmExcludeClassLoaders() {
        return asmExcludeClassLoaders;
    }

    public void setAsmExcludeClassLoaders(String asmExcludeClassLoaders) {
        this.asmExcludeClassLoaders = asmExcludeClassLoaders;
    }

    public boolean isPrintDebugLog() {
        return printDebugLog;
    }

    public void setPrintDebugLog(boolean printDebugLog) {
        this.printDebugLog = printDebugLog;
    }

    public String getAsmProfilingType() {
        return asmProfilingType;
    }

    public boolean profilingByProfiler() {
        return asmProfilingType.equals(PropertyValues.ASM_PROFILING_TYPE_PROFILER);
    }

    public void setAsmProfilingType(String asmProfilingType) {
        this.asmProfilingType = asmProfilingType;
    }

    public String getAsmExcludeMethods() {
        return asmExcludeMethods;
    }

    public void setAsmExcludeMethods(String asmExcludeMethods) {
        this.asmExcludeMethods = asmExcludeMethods;
    }

    public boolean isAsmExcludePrivateMethod() {
        return asmExcludePrivateMethod;
    }

    public void setAsmExcludePrivateMethod(boolean asmExcludePrivateMethod) {
        this.asmExcludePrivateMethod = asmExcludePrivateMethod;
    }

    @Override
    public String toString() {
        return "ProfilingConfig{" +
                "perStatsProcessor='" + perStatsProcessor + '\'' +
                ", recorderMode='" + recorderMode + '\'' +
                ", milliTimeSlice=" + milliTimeSlice +
                ", includePackages='" + includePackages + '\'' +
                ", excludePackages='" + excludePackages + '\'' +
                ", asmExcludeClassLoaders='" + asmExcludeClassLoaders + '\'' +
                ", printDebugLog=" + printDebugLog +
                ", asmProfilingType='" + asmProfilingType + '\'' +
                ", asmExcludeMethods='" + asmExcludeMethods + '\'' +
                ", asmExcludePrivateMethod=" + asmExcludePrivateMethod +
                '}';
    }
}
