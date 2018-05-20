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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

            Logger.info("MyASMBootstrap doInitial() SUCCESS!!!");
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
            config.setMilliTimeSlice(MyProperties.getLong(PropertyKeys.MILL_TIME_SLICE, PropertyValues.DEFAULT_TIME_SLICE));
            config.setExcludePackages(MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_PACKAGES, ""));
            config.setIncludePackages(MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_PACKAGES, ""));
            config.setPrintDebugLog(MyProperties.getBoolean(PropertyKeys.DEBUG_PRINT_DEBUG_LOG, false));
            config.setAsmProfilingType(MyProperties.getStr(PropertyKeys.ASM_PROFILING_TYPE, PropertyValues.ASM_PROFILING_TYPE_PACKAGE));
            config.setAsmExcludeMethods(MyProperties.getStr(PropertyKeys.ASM_FILTER_EXCLUDE_METHODS, ""));
            config.setAsmExcludePrivateMethod(MyProperties.getBoolean(PropertyKeys.ASM_EXCLUDE_PRIVATE_METHODS, true));
            config.setAsmExcludeClassLoaders(MyProperties.getStr(PropertyKeys.ASM_FILTER_INCLUDE_CLASS_LOADERS, ""));
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
            String excludeClassLoaders = ProfilingConfig.getInstance().getAsmExcludeClassLoaders();
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
            String includePackages = ProfilingConfig.getInstance().getAsmExcludeMethods();
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
                        Map<String, AbstractRecorder> recorderMap = maintainer.getRecorderMap();
                        List<PerfStats> perfStatsList = new ArrayList<>(recorderMap.size());
                        AbstractRecorder recorder = null;
                        for (Map.Entry<String, AbstractRecorder> entry : recorderMap.entrySet()) {
                            recorder = entry.getValue();
                            perfStatsList.add(PerfStatsCalculator.calPerfStats(recorder));
                        }

                        if (recorder != null) {
                            processor.process(perfStatsList, recorder.getStartTime(), recorder.getStopTime());
                        }

                        ThreadPoolExecutor executor = processor.getExecutor();
                        executor.shutdown();
                        executor.awaitTermination(30, TimeUnit.SECONDS);
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
