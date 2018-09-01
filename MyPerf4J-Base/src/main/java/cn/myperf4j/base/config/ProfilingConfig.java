package cn.myperf4j.base.config;

import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.MapUtils;

import java.util.Map;

/**
 * Created by LinShunkang on 2018/5/12
 */
public class ProfilingConfig {

    private static final ProfilingConfig instance = new ProfilingConfig();

    private String appName;

    private String methodMetricsProcessor;

    private String classMetricsProcessor;

    private String gcMetricsProcessor;

    private String memoryMetricsProcessor;

    private String threadMetricsProcessor;

    private String recorderMode;

    private int backupRecorderCount;

    private long milliTimeSlice;

    private String includePackages;

    private String excludePackages;

    private String excludeClassLoaders;

    private boolean printDebugLog;

    private String excludeMethods;

    private boolean excludePrivateMethod;

    private String profilingParamsFile;

    private ProfilingParams commonProfilingParams;

    private Map<String, ProfilingParams> profilingParamsMap = MapUtils.createHashMap(100);


    /**
     * singleton pattern
     */
    public static ProfilingConfig getInstance() {
        return instance;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMethodMetricsProcessor() {
        return methodMetricsProcessor;
    }

    public void setMethodMetricsProcessor(String methodMetricsProcessor) {
        this.methodMetricsProcessor = methodMetricsProcessor;
    }

    public String getClassMetricsProcessor() {
        return classMetricsProcessor;
    }

    public void setClassMetricsProcessor(String classMetricsProcessor) {
        this.classMetricsProcessor = classMetricsProcessor;
    }

    public String getGcMetricsProcessor() {
        return gcMetricsProcessor;
    }

    public void setGcMetricsProcessor(String gcMetricsProcessor) {
        this.gcMetricsProcessor = gcMetricsProcessor;
    }

    public String getMemoryMetricsProcessor() {
        return memoryMetricsProcessor;
    }

    public void setMemoryMetricsProcessor(String memoryMetricsProcessor) {
        this.memoryMetricsProcessor = memoryMetricsProcessor;
    }

    public String getThreadMetricsProcessor() {
        return threadMetricsProcessor;
    }

    public void setThreadMetricsProcessor(String threadMetricsProcessor) {
        this.threadMetricsProcessor = threadMetricsProcessor;
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

    public int getBackupRecorderCount() {
        return backupRecorderCount;
    }

    public void setBackupRecorderCount(int backupRecorderCount) {
        this.backupRecorderCount = backupRecorderCount;
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

    public String getExcludeClassLoaders() {
        return excludeClassLoaders;
    }

    public void setExcludeClassLoaders(String excludeClassLoaders) {
        this.excludeClassLoaders = excludeClassLoaders;
    }

    public boolean isPrintDebugLog() {
        return printDebugLog;
    }

    public void setPrintDebugLog(boolean printDebugLog) {
        this.printDebugLog = printDebugLog;
    }

    public String getExcludeMethods() {
        return excludeMethods;
    }

    public void setExcludeMethods(String excludeMethods) {
        this.excludeMethods = excludeMethods;
    }

    public boolean isExcludePrivateMethod() {
        return excludePrivateMethod;
    }

    public void setExcludePrivateMethod(boolean excludePrivateMethod) {
        this.excludePrivateMethod = excludePrivateMethod;
    }

    public String getProfilingParamsFile() {
        return profilingParamsFile;
    }

    public void setProfilingParamsFile(String profilingParamsFile) {
        this.profilingParamsFile = profilingParamsFile;
    }

    public void setCommonProfilingParams(int timeThreshold, int outThresholdCount) {
        this.commonProfilingParams = ProfilingParams.of(timeThreshold, outThresholdCount);
    }

    public ProfilingParams getCommonProfilingParams() {
        return commonProfilingParams;
    }

    public void addProfilingParam(String methodName, int timeThreshold, int outThresholdCount) {
        profilingParamsMap.put(methodName, ProfilingParams.of(timeThreshold, outThresholdCount));
    }

    public ProfilingParams getProfilingParam(String methodName) {
        ProfilingParams params = profilingParamsMap.get(methodName);
        if (params != null) {
            return params;
        }

        return commonProfilingParams;
    }

    @Override
    public String toString() {
        return "ProfilingConfig{" +
                "appName='" + appName + '\'' +
                ", methodMetricsProcessor='" + methodMetricsProcessor + '\'' +
                ", classMetricsProcessor='" + classMetricsProcessor + '\'' +
                ", gcMetricsProcessor='" + gcMetricsProcessor + '\'' +
                ", memoryMetricsProcessor='" + memoryMetricsProcessor + '\'' +
                ", threadMetricsProcessor='" + threadMetricsProcessor + '\'' +
                ", recorderMode='" + recorderMode + '\'' +
                ", backupRecorderCount=" + backupRecorderCount +
                ", milliTimeSlice=" + milliTimeSlice +
                ", includePackages='" + includePackages + '\'' +
                ", excludePackages='" + excludePackages + '\'' +
                ", excludeClassLoaders='" + excludeClassLoaders + '\'' +
                ", printDebugLog=" + printDebugLog +
                ", excludeMethods='" + excludeMethods + '\'' +
                ", excludePrivateMethod=" + excludePrivateMethod +
                ", profilingParamsFile='" + profilingParamsFile + '\'' +
                ", commonProfilingParams=" + commonProfilingParams +
                ", profilingParamsMap=" + profilingParamsMap +
                '}';
    }
}
