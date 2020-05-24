package cn.myperf4j.base.config;

import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.MapUtils;

import java.util.Map;

/**
 * Created by LinShunkang on 2018/5/12
 */
public class ProfilingConfig {

    private static final ProfilingConfig instance = new ProfilingConfig();

    private MetricsConfig metricsConfig;

    private FilterConfig filterConfig;

    private InfluxDBConfig influxDBConfig;

    private String appName;

    private String configFileDir;

    private int metricsProcessorType;

    private String methodMetricsFile;

    private String classMetricsFile;

    private String gcMetricsFile;

    private String memoryMetricsFile;

    private String bufferPoolMetricsFile;

    private String threadMetricsFile;

    private String compilationMetricsFile;

    private String fileDescMetricsFile;

    private String logRollingTimeUnit;

    private int LogReserveCount;

    private String recorderMode;

    private int backupRecorderCount;

    private long milliTimeSlice;

    private long methodMilliTimeSlice;

    private long jvmMilliTimeSlice;

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

    private final Map<String, ProfilingParams> profilingParamsMap = MapUtils.createHashMap(100);


    /**
     * singleton pattern
     */
    public static ProfilingConfig getInstance() {
        return instance;
    }

    public InfluxDBConfig influxDBConfig() {
        return influxDBConfig;
    }

    public void influxDBConfig(InfluxDBConfig influxDBConfig) {
        this.influxDBConfig = influxDBConfig;
    }

    public MetricsConfig metricsConfig() {
        return metricsConfig;
    }

    public void metricsConfig(MetricsConfig metricsConfig) {
        this.metricsConfig = metricsConfig;
    }

    public FilterConfig filterConfig() {
        return filterConfig;
    }

    public void filterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
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

    public String getCompilationMetricsFile() {
        return compilationMetricsFile;
    }

    public void setCompilationMetricsFile(String compilationMetricsFile) {
        this.compilationMetricsFile = compilationMetricsFile;
    }

    public String getFileDescMetricsFile() {
        return fileDescMetricsFile;
    }

    public void setFileDescMetricsFile(String fileDescMetricsFile) {
        this.fileDescMetricsFile = fileDescMetricsFile;
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

    public long getMethodMilliTimeSlice() {
        return methodMilliTimeSlice;
    }

    public void setMethodMilliTimeSlice(long methodMilliTimeSlice) {
        this.methodMilliTimeSlice = methodMilliTimeSlice;
    }

    public long getJvmMilliTimeSlice() {
        return jvmMilliTimeSlice;
    }

    public void setJvmMilliTimeSlice(long jvmMilliTimeSlice) {
        this.jvmMilliTimeSlice = jvmMilliTimeSlice;
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
        return configFileDir + "." + appName + "_SysGenProfilingFile";
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
                ", fileDescMetricsFile='" + fileDescMetricsFile + '\'' +
                ", compilationMetricsFile='" + compilationMetricsFile + '\'' +
                ", logRollingTimeUnit='" + logRollingTimeUnit + '\'' +
                ", LogReserveCount=" + LogReserveCount +
                ", recorderMode='" + recorderMode + '\'' +
                ", backupRecorderCount=" + backupRecorderCount +
                ", milliTimeSlice=" + milliTimeSlice +
                ", methodMilliTimeSlice=" + methodMilliTimeSlice +
                ", jvmMilliTimeSlice=" + jvmMilliTimeSlice +
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

    public static class InfluxDBConfig {

        private String host;

        private int port;

        private String database;

        private int connectTimeout;

        private int readTimeout;

        private String username;

        private String password;

        public String host() {
            return host;
        }

        public void host(String host) {
            this.host = host;
        }

        public int port() {
            return port;
        }

        public void port(int port) {
            this.port = port;
        }

        public String database() {
            return database;
        }

        public void database(String database) {
            this.database = database;
        }

        public int connectTimeout() {
            return connectTimeout;
        }

        public void connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public int readTimeout() {
            return readTimeout;
        }

        public void readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
        }

        public String username() {
            return username;
        }

        public void username(String username) {
            this.username = username;
        }

        public String password() {
            return password;
        }

        public void password(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "InfluxDBConfig{" +
                    "ip='" + host + '\'' +
                    ", port=" + port +
                    ", database='" + database + '\'' +
                    ", connectTimeout=" + connectTimeout +
                    ", readTimeout=" + readTimeout +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }

    public static class MetricsConfig {

        private int metricsProcessorType;

        private String methodMetricsLog;

        private String classMetricsLog;

        private String gcMetricsLog;

        private String memoryMetricsLog;

        private String bufferPoolMetricsLog;

        private String threadMetricsLog;

        private String compilationMetricsLog;

        private String fileDescMetricsLOg;

        private long methodMilliTimeSlice;

        private long jvmMilliTimeSlice;

        public int metricsProcessorType() {
            return metricsProcessorType;
        }

        public void metricsProcessorType(int metricsProcessorType) {
            this.metricsProcessorType = metricsProcessorType;
        }

        public String methodMetricsFile() {
            return methodMetricsLog;
        }

        public void methodMetricsFile(String methodMetricsFile) {
            this.methodMetricsLog = methodMetricsFile;
        }

        public String classMetricsFile() {
            return classMetricsLog;
        }

        public void classMetricsFile(String classMetricsFile) {
            this.classMetricsLog = classMetricsFile;
        }

        public String gcMetricsFile() {
            return gcMetricsLog;
        }

        public void gcMetricsFile(String gcMetricsFile) {
            this.gcMetricsLog = gcMetricsFile;
        }

        public String memoryMetricsFile() {
            return memoryMetricsLog;
        }

        public void memoryMetricsFile(String memoryMetricsFile) {
            this.memoryMetricsLog = memoryMetricsFile;
        }

        public String bufferPoolMetricsFile() {
            return bufferPoolMetricsLog;
        }

        public void bufferPoolMetricsFile(String bufferPoolMetricsFile) {
            this.bufferPoolMetricsLog = bufferPoolMetricsFile;
        }

        public String threadMetricsFile() {
            return threadMetricsLog;
        }

        public void threadMetricsFile(String threadMetricsFile) {
            this.threadMetricsLog = threadMetricsFile;
        }

        public String compilationMetricsFile() {
            return compilationMetricsLog;
        }

        public void compilationMetricsFile(String compilationMetricsFile) {
            this.compilationMetricsLog = compilationMetricsFile;
        }

        public String fileDescMetricsFile() {
            return fileDescMetricsLOg;
        }

        public void fileDescMetricsFile(String fileDescMetricsFile) {
            this.fileDescMetricsLOg = fileDescMetricsFile;
        }

        public long methodMilliTimeSlice() {
            return methodMilliTimeSlice;
        }

        public void methodMilliTimeSlice(long methodMilliTimeSlice) {
            this.methodMilliTimeSlice = methodMilliTimeSlice;
        }

        public long jvmMilliTimeSlice() {
            return jvmMilliTimeSlice;
        }

        public void jvmMilliTimeSlice(long jvmMilliTimeSlice) {
            this.jvmMilliTimeSlice = jvmMilliTimeSlice;
        }

        @Override
        public String toString() {
            return "MetricsConfig{" +
                    "metricsProcessorType=" + metricsProcessorType +
                    ", methodMetricsFile='" + methodMetricsLog + '\'' +
                    ", classMetricsFile='" + classMetricsLog + '\'' +
                    ", gcMetricsFile='" + gcMetricsLog + '\'' +
                    ", memoryMetricsFile='" + memoryMetricsLog + '\'' +
                    ", bufferPoolMetricsFile='" + bufferPoolMetricsLog + '\'' +
                    ", threadMetricsFile='" + threadMetricsLog + '\'' +
                    ", compilationMetricsFile='" + compilationMetricsLog + '\'' +
                    ", fileDescMetricsFile='" + fileDescMetricsLOg + '\'' +
                    ", methodMilliTimeSlice=" + methodMilliTimeSlice +
                    ", jvmMilliTimeSlice=" + jvmMilliTimeSlice +
                    '}';
        }
    }

    public static class FilterConfig {

        private String excludeClassLoaders;

        private String includePackages;

        private String excludePackages;

        private boolean showMethodParams;

        private String excludeMethods;

        private boolean excludePrivateMethod;

        public String excludeClassLoaders() {
            return excludeClassLoaders;
        }

        public void excludeClassLoaders(String excludeClassLoaders) {
            this.excludeClassLoaders = excludeClassLoaders;
        }

        public String includePackages() {
            return includePackages;
        }

        public void includePackages(String includePackages) {
            this.includePackages = includePackages;
        }

        public String excludePackages() {
            return excludePackages;
        }

        public void excludePackages(String excludePackages) {
            this.excludePackages = excludePackages;
        }

        public boolean showMethodParams() {
            return showMethodParams;
        }

        public void showMethodParams(boolean showMethodParams) {
            this.showMethodParams = showMethodParams;
        }

        public String excludeMethods() {
            return excludeMethods;
        }

        public void excludeMethods(String excludeMethods) {
            this.excludeMethods = excludeMethods;
        }

        public boolean excludePrivateMethod() {
            return excludePrivateMethod;
        }

        public void excludePrivateMethod(boolean excludePrivateMethod) {
            this.excludePrivateMethod = excludePrivateMethod;
        }

        @Override
        public String toString() {
            return "FilterConfig{" +
                    "excludeClassLoaders='" + excludeClassLoaders + '\'' +
                    ", includePackages='" + includePackages + '\'' +
                    ", excludePackages='" + excludePackages + '\'' +
                    ", showMethodParams=" + showMethodParams +
                    ", excludeMethods='" + excludeMethods + '\'' +
                    ", excludePrivateMethod=" + excludePrivateMethod +
                    '}';
        }
    }
}
