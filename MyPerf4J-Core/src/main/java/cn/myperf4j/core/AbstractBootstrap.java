package cn.myperf4j.core;

import cn.myperf4j.base.config.LevelMappingFilter;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.metric.processor.*;
import cn.myperf4j.base.util.ExecutorManager;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.util.StrUtils;
import cn.myperf4j.core.recorder.AbstractRecorderMaintainer;
import cn.myperf4j.core.scheduler.JvmMetricsScheduler;
import cn.myperf4j.base.Scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/4/11
 */
public abstract class AbstractBootstrap {

    protected MethodMetricsProcessor processor;

    protected AbstractRecorderMaintainer maintainer;

    public final boolean initial() {
        try {
            Logger.info("Thanks sincerely for using MyPerf4J.");
            if (!doInitial()) {
                Logger.error("AbstractBootstrap doInitial() FAILURE!!!");
                return false;
            }

            printBannerText();
            Logger.info("AbstractBootstrap doInitial() SUCCESS!!!");
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initial()", e);
        }
        return false;
    }

    private boolean doInitial() {
        if (!initProperties()) {
            Logger.error("AbstractBootstrap initProperties() FAILURE!!!");
            return false;
        }

        if (!initProfilingConfig()) {
            Logger.error("AbstractBootstrap initProfilingConfig() FAILURE!!!");
            return false;
        }

        if (!initLogger()) {
            Logger.error("AbstractBootstrap initLogger() FAILURE!!!");
            return false;
        }

        if (!initPackageFilter()) {
            Logger.error("AbstractBootstrap initPackageFilter() FAILURE!!!");
            return false;
        }

        if (!initClassLoaderFilter()) {
            Logger.error("AbstractBootstrap initClassLoaderFilter() FAILURE!!!");
            return false;
        }

        if (!initMethodFilter()) {
            Logger.error("AbstractBootstrap initMethodFilter() FAILURE!!!");
            return false;
        }

        if (!initClassLevelMapping()) {
            Logger.error("AbstractBootstrap initClassLevelMapping() FAILURE!!!");
            return false;
        }

        if (!initPerfStatsProcessor()) {
            Logger.error("AbstractBootstrap initPerfStatsProcessor() FAILURE!!!");
            return false;
        }

        if (!initProfilingParams()) {
            Logger.error("AbstractBootstrap initProfilingParams() FAILURE!!!");
            return false;
        }

        if (!initRecorderMaintainer()) {
            Logger.error("AbstractBootstrap initRecorderMaintainer() FAILURE!!!");
            return false;
        }

        if (!initShutDownHook()) {
            Logger.error("AbstractBootstrap initShutDownHook() FAILURE!!!");
            return false;
        }

        if (!initScheduler()) {
            Logger.error("AbstractBootstrap initScheduler() FAILURE!!!");
            return false;
        }

        if (!initOther()) {
            Logger.error("AbstractBootstrap initOther() FAILURE!!!");
            return false;
        }
        return true;
    }

    private boolean initProperties() {
        String configFilePath = System.getProperty(PropertyKeys.PRO_FILE_NAME, PropertyValues.DEFAULT_PRO_FILE);
        try (InputStream in = new FileInputStream(configFilePath)) {
            Properties properties = new Properties();
            properties.load(in);

            properties.put(PropertyKeys.PRO_FILE_DIR, getConfigFileDir(configFilePath));
            return MyProperties.initial(properties);
        } catch (IOException e) {
            Logger.error("AbstractBootstrap.initProperties()", e);
        }
        return false;
    }

    private String getConfigFileDir(String configFilePath) {
        if (System.getProperty("os.name").startsWith("windows")) {
            int idx = configFilePath.lastIndexOf('\\');
            return configFilePath.substring(0, idx + 1);
        }

        int idx = configFilePath.lastIndexOf('/');
        return configFilePath.substring(0, idx + 1);
    }

    private boolean initProfilingConfig() {
        try {
            ProfilingConfig config = ProfilingConfig.getInstance();

            initAppName(config);
            initMetricsFileConfig(config);
            initLogConfig(config);
            initTimeSliceConfig(config);
            initRecorderConfig(config);
            initFiltersConfig(config);
            initProfilingParamsConfig(config);

            config.setConfigFileDir(MyProperties.getStr(PropertyKeys.PRO_FILE_DIR));
            config.setClassLevelMappings(MyProperties.getStr(PropertyKeys.CLASS_LEVEL_MAPPING, ""));
            config.setShowMethodParams(MyProperties.getBoolean(PropertyKeys.SHOW_METHOD_PARAMS, false));
            config.setPrintDebugLog(MyProperties.getBoolean(PropertyKeys.DEBUG_PRINT_DEBUG_LOG, false));

            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initProfilingConfig()", e);
        }
        return false;
    }

    private void initAppName(ProfilingConfig config) {
        String appName = MyProperties.getStr(PropertyKeys.APP_NAME);
        if (StrUtils.isBlank(appName)) {
            throw new IllegalArgumentException("AppName is required!!!");
        }
        config.setAppName(appName);
    }

    private void initMetricsFileConfig(ProfilingConfig config) {
        config.setMetricsProcessorType(MyProperties.getInt(PropertyKeys.METRICS_PROCESS_TYPE, PropertyValues.METRICS_PROCESS_TYPE_STDOUT));
        if (config.getMetricsProcessorType() == PropertyValues.METRICS_PROCESS_TYPE_STDOUT) {
            config.setMethodMetricsFile(PropertyValues.STDOUT_FILE);
            config.setClassMetricsFile(PropertyValues.STDOUT_FILE);
            config.setGcMetricsFile(PropertyValues.STDOUT_FILE);
            config.setMemoryMetricsFile(PropertyValues.STDOUT_FILE);
            config.setBufferPoolMetricsFile(PropertyValues.STDOUT_FILE);
            config.setThreadMetricsFile(PropertyValues.STDOUT_FILE);
        } else {
            config.setMethodMetricsFile(MyProperties.getStr(PropertyKeys.METHOD_METRICS_FILE, PropertyValues.DEFAULT_METRICS_FILE));
            config.setClassMetricsFile(MyProperties.getStr(PropertyKeys.CLASS_METRICS_FILE, PropertyValues.NULL_FILE));
            config.setGcMetricsFile(MyProperties.getStr(PropertyKeys.GC_METRICS_FILE, PropertyValues.NULL_FILE));
            config.setMemoryMetricsFile(MyProperties.getStr(PropertyKeys.MEM_METRICS_FILE, PropertyValues.NULL_FILE));
            config.setBufferPoolMetricsFile(MyProperties.getStr(PropertyKeys.BUF_POOL_METRICS_FILE, PropertyValues.NULL_FILE));
            config.setThreadMetricsFile(MyProperties.getStr(PropertyKeys.THREAD_METRICS_FILE, PropertyValues.NULL_FILE));
        }
    }

    private void initLogConfig(ProfilingConfig config) {
        config.setLogRollingTimeUnit(MyProperties.getStr(PropertyKeys.LOG_ROLLING_TIME_TIME_UNIT, PropertyValues.LOG_ROLLING_TIME_DAILY));
        config.setLogReserveCount(MyProperties.getInt(PropertyKeys.LOG_RESERVE_COUNT, PropertyValues.DEFAULT_LOG_RESERVE_COUNT));
    }

    private void initTimeSliceConfig(ProfilingConfig config) {
        config.setMilliTimeSlice(MyProperties.getLong(PropertyKeys.MILLI_TIME_SLICE, PropertyValues.DEFAULT_TIME_SLICE));
        config.setMethodMilliTimeSlice(MyProperties.getLong(PropertyKeys.METHOD_MILLI_TIME_SLICE, config.getMilliTimeSlice()));
        config.setJvmMilliTimeSlice(MyProperties.getLong(PropertyKeys.JVM_MILLI_TIME_SLICE, config.getMilliTimeSlice()));
    }

    private void initRecorderConfig(ProfilingConfig config) {
        config.setRecorderMode(MyProperties.getStr(PropertyKeys.RECORDER_MODE, PropertyValues.RECORDER_MODE_ROUGH));
        config.setBackupRecorderCount(MyProperties.getInt(PropertyKeys.BACKUP_RECORDERS_COUNT, PropertyValues.MIN_BACKUP_RECORDERS_COUNT));
    }

    private void initFiltersConfig(ProfilingConfig config) {
        String includePackages = MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_PACKAGES, "");
        if (StrUtils.isBlank(includePackages)) {
            throw new IllegalArgumentException("IncludePackages is required!!!");
        }

        config.setIncludePackages(includePackages);
        config.setExcludePackages(MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_PACKAGES, ""));
        config.setExcludeMethods(MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_METHODS, ""));
        config.setExcludePrivateMethod(MyProperties.getBoolean(PropertyKeys.EXCLUDE_PRIVATE_METHODS, true));
        config.setExcludeClassLoaders(MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_CLASS_LOADERS, ""));
    }

    private void initProfilingParamsConfig(ProfilingConfig config) {
        config.setProfilingParamsFile(MyProperties.getStr(PropertyKeys.PROFILING_PARAMS_FILE_NAME, ""));
        config.setCommonProfilingParams(MyProperties.getInt(PropertyKeys.PROFILING_TIME_THRESHOLD, 1000), MyProperties.getInt(PropertyKeys.PROFILING_OUT_THRESHOLD_COUNT, 16));
    }

    private boolean initLogger() {
        try {
            Logger.setDebugEnable(ProfilingConfig.getInstance().isPrintDebugLog());
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initLogger()", e);
        }
        return false;
    }

    private boolean initPackageFilter() {
        try {
            String includePackages = ProfilingConfig.getInstance().getIncludePackages();
            String[] includeArr = includePackages.split(PropertyValues.FILTER_SEPARATOR);
            if (includeArr.length > 0) {
                for (String pkg : includeArr) {
                    ProfilingFilter.addIncludePackage(pkg);
                }
            }

            String excludePackages = ProfilingConfig.getInstance().getExcludePackages();
            String[] excludeArr = excludePackages.split(PropertyValues.FILTER_SEPARATOR);
            if (excludeArr.length > 0) {
                for (String pkg : excludeArr) {
                    ProfilingFilter.addExcludePackage(pkg);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initPackageFilter()", e);
        }
        return false;
    }

    private boolean initClassLoaderFilter() {
        try {
            String excludeClassLoaders = ProfilingConfig.getInstance().getExcludeClassLoaders();
            String[] excludeArr = excludeClassLoaders.split(PropertyValues.FILTER_SEPARATOR);
            if (excludeArr.length > 0) {
                for (String classLoader : excludeArr) {
                    ProfilingFilter.addExcludeClassLoader(classLoader);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initClassLoaderFilter()", e);
        }
        return false;
    }

    private boolean initMethodFilter() {
        try {
            String includePackages = ProfilingConfig.getInstance().getExcludeMethods();
            String[] excludeArr = includePackages.split(PropertyValues.FILTER_SEPARATOR);
            if (excludeArr.length > 0) {
                for (String method : excludeArr) {
                    ProfilingFilter.addExcludeMethods(method);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initMethodFilter()", e);
        }
        return false;
    }

    //MethodLevelMapping=Controller:[*Controller];Api:[*Api,*ApiImpl];
    private boolean initClassLevelMapping() {
        try {
            String levelMappings = ProfilingConfig.getInstance().getClassLevelMappings();
            if (StrUtils.isBlank(levelMappings)) {
                Logger.info("ClassLevelMapping is blank, so use default mappings.");
                return true;
            }

            List<String> mappingPairs = StrUtils.splitAsList(levelMappings, ';');
            for (int i = 0; i < mappingPairs.size(); ++i) {
                String mappingPair = mappingPairs.get(i);
                List<String> mappingPairList = StrUtils.splitAsList(mappingPair, ':');
                if (mappingPairList.size() != 2) {
                    Logger.warn("MethodLevelMapping is not correct: " + mappingPair);
                    continue;
                }

                LevelMappingFilter.putLevelMapping(mappingPairList.get(0), getMappingExpList(mappingPairList.get(1)));
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initClassLevelMapping()", e);
        }
        return false;
    }

    //Api:[*Api,*ApiImpl]
    private List<String> getMappingExpList(String expStr) {
        expStr = expStr.substring(1, expStr.length() - 1);
        return StrUtils.splitAsList(expStr, ',');
    }

    private boolean initPerfStatsProcessor() {
        try {
            int processorType = ProfilingConfig.getInstance().getMetricsProcessorType();
            processor = MetricsProcessorFactory.getMethodMetricsProcessor(processorType);
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initPerfStatsProcessor()", e);
        }
        return false;
    }

    private boolean initProfilingParams() {
        try {
            ProfilingConfig config = ProfilingConfig.getInstance();
            String sysProfilingParamFile = config.getSysProfilingParamsFile();
            File sysFile = new File(sysProfilingParamFile);
            if (sysFile.exists() && sysFile.isFile()) {
                addProfilingParams(config, sysProfilingParamFile);
            }

            String manualProfilingParamFile = config.getProfilingParamsFile();
            if (StrUtils.isNotBlank(manualProfilingParamFile)) {
                addProfilingParams(config, manualProfilingParamFile);
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initProfilingParams()", e);
        }
        return false;
    }

    private void addProfilingParams(ProfilingConfig config, String profilingParamFile) {
        try (InputStream in = new FileInputStream(profilingParamFile)) {
            Properties properties = new Properties();
            properties.load(in);

            Set<String> keys = properties.stringPropertyNames();
            for (String key : keys) {
                String value = properties.getProperty(key);
                if (value == null) {
                    continue;
                }

                List<String> strList = StrUtils.splitAsList(value, ':');
                if (strList.size() != 2) {
                    continue;
                }

                int timeThreshold = getInt(strList.get(0).trim(), 1000);
                int outThresholdCount = getInt(strList.get(1).trim(), 64);
                config.addProfilingParam(key.replace('.', '/'), timeThreshold, outThresholdCount);
            }
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.addProfilingParams(" + profilingParamFile + ")", e);
        }
    }

    private int getInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.getInt(" + str + ")", e);
        }
        return defaultValue;
    }

    private boolean initRecorderMaintainer() {
        return (maintainer = doInitRecorderMaintainer()) != null;
    }

    public abstract AbstractRecorderMaintainer doInitRecorderMaintainer();

    private boolean initShutDownHook() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    Logger.info("ENTER ShutdownHook...");
                    try {
                        MethodMetricsHistogram.buildSysGenProfilingFile();
                        ExecutorManager.stopAll(6, TimeUnit.SECONDS);
                    } finally {
                        Logger.info("EXIT ShutdownHook...");
                    }
                }
            }));
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initShutDownHook()", e);
        }
        return false;
    }

    private boolean initScheduler() {
        try {
            ProfilingConfig config = ProfilingConfig.getInstance();
            LightWeightScheduler.dispatchScheduleTask(maintainer, config.getMethodMilliTimeSlice());
            LightWeightScheduler.dispatchScheduleTask(jvmMetricsScheduler(), config.getJvmMilliTimeSlice());
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initScheduler()", e);
        }
        return false;
    }

    private Scheduler jvmMetricsScheduler() {
        int processorType = ProfilingConfig.getInstance().getMetricsProcessorType();
        JvmClassMetricsProcessor classProcessor = MetricsProcessorFactory.getClassMetricsProcessor(processorType);
        JvmGcMetricsProcessor gcProcessor = MetricsProcessorFactory.getGcMetricsProcessor(processorType);
        JvmMemoryMetricsProcessor memoryProcessor = MetricsProcessorFactory.getMemoryMetricsProcessor(processorType);
        JvmBufferPoolMetricsProcessor bufferPoolProcessor = MetricsProcessorFactory.getBufferPoolMetricsProcessor(processorType);
        JvmThreadMetricsProcessor threadProcessor = MetricsProcessorFactory.getThreadMetricsProcessor(processorType);
        return new JvmMetricsScheduler(classProcessor, gcProcessor, memoryProcessor, bufferPoolProcessor, threadProcessor);
    }

    public abstract boolean initOther();

    private void printBannerText() {
        Logger.info(LINE_SEPARATOR +
                "    __  ___      ____            ______ __      __" + LINE_SEPARATOR +
                "   /  |/  /_  __/ __ \\___  _____/ __/ // /     / /" + LINE_SEPARATOR +
                "  / /|_/ / / / / /_/ / _ \\/ ___/ /_/ // /___  / / " + LINE_SEPARATOR +
                " / /  / / /_/ / ____/  __/ /  / __/__  __/ /_/ /  " + LINE_SEPARATOR +
                "/_/  /_/\\__, /_/    \\___/_/  /_/    /_/  \\____/   " + LINE_SEPARATOR +
                "       /____/                                     " + LINE_SEPARATOR);
    }
}
