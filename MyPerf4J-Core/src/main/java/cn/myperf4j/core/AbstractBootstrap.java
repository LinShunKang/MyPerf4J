package cn.myperf4j.core;

import cn.myperf4j.base.PerfStats;
import cn.myperf4j.base.PerfStatsProcessor;
import cn.myperf4j.core.config.ProfilingConfig;
import cn.myperf4j.core.config.ProfilingFilter;
import cn.myperf4j.core.constant.PropertyKeys;
import cn.myperf4j.core.constant.PropertyValues;
import cn.myperf4j.core.util.IOUtils;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.config.MyProperties;
import cn.myperf4j.core.util.PerfStatsCalculator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/4/11
 */
public abstract class AbstractBootstrap {

    protected AsyncPerfStatsProcessor processor;

    protected AbstractRecorderMaintainer maintainer;

    public final boolean initial() {
        try {
            if (!doInitial()) {
                Logger.error("AbstractBootstrap doInitial() FAILURE!!!");
                return false;
            }

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

        if (!initOther()) {
            Logger.error("AbstractBootstrap initOther() FAILURE!!!");
            return false;
        }
        return true;
    }

    private boolean initProperties() {
        InputStream in = null;
        try {
            String configFilePath = System.getProperty(PropertyKeys.PRO_FILE_NAME, PropertyValues.DEFAULT_PRO_FILE);
            in = new FileInputStream(configFilePath);

            Properties properties = new Properties();
            properties.load(in);
            return MyProperties.initial(properties);
        } catch (IOException e) {
            Logger.error("AbstractBootstrap.initProperties()", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return false;
    }

    private boolean initProfilingConfig() {
        try {
            ProfilingConfig config = ProfilingConfig.getInstance();
            config.setPerStatsProcessor(MyProperties.getStr(PropertyKeys.PERF_STATS_PROCESSOR, PropertyValues.DEFAULT_PERF_STATS_PROCESSOR));
            config.setRecorderMode(MyProperties.getStr(PropertyKeys.RECORDER_MODE, PropertyValues.RECORDER_MODE_ROUGH));
            config.setBackupRecorderCount(MyProperties.getInt(PropertyKeys.BACKUP_RECORDERS_COUNT, PropertyValues.MIN_BACKUP_RECORDERS_COUNT));
            config.setMilliTimeSlice(MyProperties.getLong(PropertyKeys.MILL_TIME_SLICE, PropertyValues.DEFAULT_TIME_SLICE));
            config.setExcludePackages(MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_PACKAGES, ""));
            config.setIncludePackages(MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_PACKAGES, ""));
            config.setPrintDebugLog(MyProperties.getBoolean(PropertyKeys.DEBUG_PRINT_DEBUG_LOG, false));
            config.setExcludeMethods(MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_METHODS, ""));
            config.setExcludePrivateMethod(MyProperties.getBoolean(PropertyKeys.EXCLUDE_PRIVATE_METHODS, true));
            config.setExcludeClassLoaders(MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_CLASS_LOADERS, ""));
            config.setProfilingParamsFile(MyProperties.getStr(PropertyKeys.PROFILING_PARAMS_FILE_NAME, ""));
            config.setCommonProfilingParams(MyProperties.getInt(PropertyKeys.PROFILING_TIME_THRESHOLD, 500), MyProperties.getInt(PropertyKeys.PROFILING_OUT_THRESHOLD_COUNT, 50));
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initProfilingConfig()", e);
        }
        return false;
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

    private boolean initPerfStatsProcessor() {
        try {
            String className = ProfilingConfig.getInstance().getPerStatsProcessor();
            if (className == null || className.isEmpty()) {
                Logger.error("AbstractBootstrap.initPerfStatsProcessor() MyPerf4J.PSP NOT FOUND!!!");
                return false;
            }

            Class<?> clazz = AbstractBootstrap.class.getClassLoader().loadClass(className);
            Object obj = clazz.newInstance();
            if (!(obj instanceof PerfStatsProcessor)) {
                Logger.error("AbstractBootstrap.initPerfStatsProcessor() className is not correct!!!");
                return false;
            }

            processor = AsyncPerfStatsProcessor.initial((PerfStatsProcessor) obj);
            return true;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Logger.error("AbstractBootstrap.initPerfStatsProcessor()", e);
        }
        return false;
    }

    private boolean initProfilingParams() {
        InputStream in = null;
        try {
            ProfilingConfig config = ProfilingConfig.getInstance();
            String profilingParamFile = config.getProfilingParamsFile();
            if (profilingParamFile == null || profilingParamFile.isEmpty()) {
                Logger.warn("profilingParamFile is empty!");
                return true;
            }

            in = new FileInputStream(profilingParamFile);
            Properties properties = new Properties();
            properties.load(in);

            Set<String> keys = properties.stringPropertyNames();
            for (String key : keys) {
                String value = properties.getProperty(key);
                if (value == null) {
                    continue;
                }

                String[] strings = value.split(":");
                if (strings.length != 2) {
                    continue;
                }

                int timeThreshold = getInt(strings[0].trim(), 500);
                int outThresholdCount = getInt(strings[1].trim(), 50);
                config.addProfilingParam(key.replace('.', '/'), timeThreshold, outThresholdCount);
            }

            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initProfilingParams()", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return false;
    }

    private int getInt(String str, int defaultValue) {
        try {
            return Integer.valueOf(str);
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
                        Recorders recorders = maintainer.getRecorders();
                        List<PerfStats> perfStatsList = new ArrayList<>(recorders.size());
                        int actualSize = TagMaintainer.getInstance().getTagCount();
                        for (int i = 0; i < actualSize; ++i) {
                            Recorder recorder = recorders.getRecorder(i);
                            if (recorder == null || !recorder.hasRecord()) {
                                continue;
                            }

                            perfStatsList.add(PerfStatsCalculator.calPerfStats(recorder, recorders.getStartTime(), recorders.getStopTime()));
                        }
                        processor.process(perfStatsList, actualSize, recorders.getStartTime(), recorders.getStopTime());

                        ThreadPoolExecutor executor = processor.getExecutor();
                        executor.shutdown();
                        executor.awaitTermination(5, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        Logger.error("", e);
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

    public abstract boolean initOther();

}
