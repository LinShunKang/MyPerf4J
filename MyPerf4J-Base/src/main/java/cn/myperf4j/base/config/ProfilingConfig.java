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

    private String configFileDir;

    private int metricsProcessorType;

    private String methodMetricsFile;

    private String classMetricsFile;

    private String gcMetricsFile;

    private String memoryMetricsFile;

    private String bufferPoolMetricsFile;

    private String threadMetricsFile;

    private String logRollingTimeUnit;

    private int LogReserveCount;

    private String recorderMode;

    private int backupRecorderCount;

    private long milliTimeSlice;

    private String excludeClassLoaders;

    private String includePackages;

    private String excludePackages;

    private boolean showMethodParams;

    private boolean printDebugLog;

    private String excludeMethods;

    private boolean excludePrivateMethod;

    private String classLevelMappings;

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

    public String getConfigFileDir() {
        return configFileDir;
    }

    public void setConfigFileDir(String configFileDir) {
        this.configFileDir = configFileDir;
    }

    public int getMetricsProcessorType() {
        return metricsProcessorType;
    }

    public void setMetricsProcessorType(int metricsProcessorType) {
        this.metricsProcessorType = metricsProcessorType;
    }

    public String getMethodMetricsFile() {
        return methodMetricsFile;
    }

    public void setMethodMetricsFile(String methodMetricsFile) {
        this.methodMetricsFile = methodMetricsFile;
    }

    public String getClassMetricsFile() {
        return classMetricsFile;
    }

    public void setClassMetricsFile(String classMetricsFile) {
        this.classMetricsFile = classMetricsFile;
    }

    public String getGcMetricsFile() {
        return gcMetricsFile;
    }

    public void setGcMetricsFile(String gcMetricsFile) {
        this.gcMetricsFile = gcMetricsFile;
    }

    public String getMemoryMetricsFile() {
        return memoryMetricsFile;
    }

    public void setMemoryMetricsFile(String memoryMetricsFile) {
        this.memoryMetricsFile = memoryMetricsFile;
    }

    public String getBufferPoolMetricsFile() {
        return bufferPoolMetricsFile;
    }

    public void setBufferPoolMetricsFile(String bufferPoolMetricsFile) {
        this.bufferPoolMetricsFile = bufferPoolMetricsFile;
    }

    public String getThreadMetricsFile() {
        return threadMetricsFile;
    }

    public void setThreadMetricsFile(String threadMetricsFile) {
        this.threadMetricsFile = threadMetricsFile;
    }

    public String getLogRollingTimeUnit() {
        return logRollingTimeUnit;
    }

    public void setLogRollingTimeUnit(String logRollingTimeUnit) {
        this.logRollingTimeUnit = logRollingTimeUnit;
    }

    public int getLogReserveCount() {
        return LogReserveCount;
    }

    public void setLogReserveCount(int logReserveCount) {
        LogReserveCount = logReserveCount;
    }

    public String getRecorderMode() {
        return recorderMode;
    }

    public boolean isAccurateMode() {
        return recorderMode.toUpperCase().equals(PropertyValues.RECORDER_MODE_ACCURATE);
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

    public boolean isShowMethodParams() {
        return showMethodParams;
    }

    public void setShowMethodParams(boolean showMethodParams) {
        this.showMethodParams = showMethodParams;
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

    public String getClassLevelMappings() {
        return classLevelMappings;
    }

    public ProfilingConfig setClassLevelMappings(String classLevelMappings) {
        this.classLevelMappings = classLevelMappings;
        return this;
    }

    public String getSysProfilingParamsFile() {
        return configFileDir + appName + "_SysGenProfilingFile";
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
                ", metricsProcessorType=" + metricsProcessorType +
                ", methodMetricsFile='" + methodMetricsFile + '\'' +
                ", classMetricsFile='" + classMetricsFile + '\'' +
                ", gcMetricsFile='" + gcMetricsFile + '\'' +
                ", memoryMetricsFile='" + memoryMetricsFile + '\'' +
                ", bufferPoolMetricsFile='" + bufferPoolMetricsFile + '\'' +
                ", threadMetricsFile='" + threadMetricsFile + '\'' +
                ", recorderMode='" + recorderMode + '\'' +
                ", backupRecorderCount=" + backupRecorderCount +
                ", milliTimeSlice=" + milliTimeSlice +
                ", excludeClassLoaders='" + excludeClassLoaders + '\'' +
                ", includePackages='" + includePackages + '\'' +
                ", excludePackages='" + excludePackages + '\'' +
                ", showMethodParams=" + showMethodParams +
                ", printDebugLog=" + printDebugLog +
                ", excludeMethods='" + excludeMethods + '\'' +
                ", excludePrivateMethod=" + excludePrivateMethod +
                ", classLevelMappings='" + classLevelMappings + '\'' +
                ", profilingParamsFile='" + profilingParamsFile + '\'' +
                ", commonProfilingParams=" + commonProfilingParams +
                ", profilingParamsMap=" + profilingParamsMap +
                '}';
    }
}
