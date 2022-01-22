package cn.myperf4j.base.config;

import cn.myperf4j.base.constant.PropertyValues.LegacyValues;

import static cn.myperf4j.base.config.MyProperties.getBoolean;
import static cn.myperf4j.base.config.MyProperties.getInt;
import static cn.myperf4j.base.config.MyProperties.getLong;
import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.CLASS_LEVEL_MAPPINGS;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.EXPORTER;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_BUFF_POOL;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_CLASS_LOADING;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_COMPILATION;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_FILE_DESC;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_GC;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_MEMORY;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_METHOD;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_RESERVE_COUNT;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_ROLLING_TIME_UNIT;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.LOG_THREAD;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.METHOD_SHOW_PARAMS;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.TIME_SLICE_JVM;
import static cn.myperf4j.base.constant.PropertyKeys.Metrics.TIME_SLICE_METHOD;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.DEFAULT_METRICS_FILE;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.DEFAULT_TIME_SLICE;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_LOG_INFLUX_DB;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_LOG_STANDARD;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_LOG_STDOUT;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.LOG_ROLLING_DAILY;
import static cn.myperf4j.base.constant.PropertyValues.Metrics.STDOUT_METRICS_FILE;
import static cn.myperf4j.base.util.StrUtils.isNotBlank;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class MetricsConfig {

    private String metricsExporter;

    private String methodMetricsLog;

    private String classMetricsLog;

    private String gcMetricsLog;

    private String memoryMetricsLog;

    private String bufferPoolMetricsLog;

    private String threadMetricsLog;

    private String compilationMetricsLog;

    private String fileDescMetricsLog;

    private String logRollingTimeUnit;

    private int logReserveCount;

    private long methodMilliTimeSlice;

    private long jvmMilliTimeSlice;

    private boolean showMethodParams;

    private String classLevelMapping;

    public String metricsExporter() {
        return metricsExporter;
    }

    public void metricsExporter(String metricsExporter) {
        this.metricsExporter = metricsExporter;
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
        return fileDescMetricsLog;
    }

    public void fileDescMetricsFile(String fileDescMetricsFile) {
        this.fileDescMetricsLog = fileDescMetricsFile;
    }

    public String logRollingTimeUnit() {
        return logRollingTimeUnit;
    }

    public void logRollingTimeUnit(String logRollingTimeUnit) {
        this.logRollingTimeUnit = logRollingTimeUnit;
    }

    public int logReserveCount() {
        return logReserveCount;
    }

    public void logReserveCount(int logReserveCount) {
        this.logReserveCount = logReserveCount;
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

    public boolean showMethodParams() {
        return showMethodParams;
    }

    public void showMethodParams(boolean showMethodParams) {
        this.showMethodParams = showMethodParams;
    }

    public String classLevelMapping() {
        return classLevelMapping;
    }

    public void classLevelMapping(String classLevelMapping) {
        this.classLevelMapping = classLevelMapping;
    }

    @Override
    public String toString() {
        return "MetricsConfig{" +
                "metricsExporter='" + metricsExporter + '\'' +
                ", methodMetricsLog='" + methodMetricsLog + '\'' +
                ", classMetricsLog='" + classMetricsLog + '\'' +
                ", gcMetricsLog='" + gcMetricsLog + '\'' +
                ", memoryMetricsLog='" + memoryMetricsLog + '\'' +
                ", bufferPoolMetricsLog='" + bufferPoolMetricsLog + '\'' +
                ", threadMetricsLog='" + threadMetricsLog + '\'' +
                ", compilationMetricsLog='" + compilationMetricsLog + '\'' +
                ", fileDescMetricsLog='" + fileDescMetricsLog + '\'' +
                ", logRollingTimeUnit='" + logRollingTimeUnit + '\'' +
                ", logReserveCount='" + logReserveCount + '\'' +
                ", methodMilliTimeSlice=" + methodMilliTimeSlice +
                ", jvmMilliTimeSlice=" + jvmMilliTimeSlice +
                ", showMethodParams=" + showMethodParams +
                ", classLevelMapping='" + classLevelMapping + '\'' +
                '}';
    }

    public static MetricsConfig loadMetricsConfig() {
        MetricsConfig config = new MetricsConfig();

        String exporter = getExporter();
        config.metricsExporter(exporter);

        config.methodMetricsFile(getMetricsFile(LOG_METHOD, exporter));
        config.classMetricsFile(getMetricsFile(LOG_CLASS_LOADING, exporter));
        config.gcMetricsFile(getMetricsFile(LOG_GC, exporter));
        config.memoryMetricsFile(getMetricsFile(LOG_MEMORY, exporter));
        config.bufferPoolMetricsFile(getMetricsFile(LOG_BUFF_POOL, exporter));
        config.threadMetricsFile(getMetricsFile(LOG_THREAD, exporter));
        config.compilationMetricsFile(getMetricsFile(LOG_COMPILATION, exporter));
        config.fileDescMetricsFile(getMetricsFile(LOG_FILE_DESC, exporter));

        config.logRollingTimeUnit(getStr(LOG_ROLLING_TIME_UNIT, LOG_ROLLING_DAILY));
        config.logReserveCount(getInt(LOG_RESERVE_COUNT, 7));

        config.methodMilliTimeSlice(getLong(TIME_SLICE_METHOD, DEFAULT_TIME_SLICE));
        config.jvmMilliTimeSlice(getLong(TIME_SLICE_JVM, DEFAULT_TIME_SLICE));

        config.showMethodParams(getBoolean(METHOD_SHOW_PARAMS, true));
        config.classLevelMapping(getStr(CLASS_LEVEL_MAPPINGS));
        return config;
    }

    private static String getExporter() {
        final String exporter = getStr(EXPORTER.key());
        if (isNotBlank(exporter)) {
            return exporter;
        }

        final Integer processorType = getInt(EXPORTER.legacyKey());
        if (processorType == null) {
            return EXPORTER_LOG_STDOUT;
        }

        switch (processorType) {
            case LegacyValues.METRICS_PROCESS_TYPE_LOGGER:
                return EXPORTER_LOG_STANDARD;
            case LegacyValues.METRICS_PROCESS_TYPE_INFLUX_DB:
                return EXPORTER_LOG_INFLUX_DB;
            default:
                return EXPORTER_LOG_STDOUT;
        }
    }

    private static String getMetricsFile(ConfigKey configKey, String exporter) {
        if (EXPORTER_LOG_STDOUT.equals(exporter)) {
            return STDOUT_METRICS_FILE;
        }
        return getStr(configKey, DEFAULT_METRICS_FILE);
    }
}
