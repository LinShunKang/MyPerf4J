package cn.myperf4j.core;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.metric.processor.*;
import cn.myperf4j.base.util.IOUtils;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.core.scheduler.JvmMetricsScheduler;
import cn.myperf4j.core.scheduler.Scheduler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/4/11
 */
public abstract class AbstractBootstrap {

    protected AsyncMethodMetricsProcessor processor;

    protected AbstractRecorderMaintainer maintainer;

    public final boolean initial() {
        try {
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
            config.setMethodMetricsProcessor(MyProperties.getStr(PropertyKeys.METHOD_METRICS_PROCESSOR, PropertyValues.DEFAULT_METHOD_PROCESSOR));
            config.setClassMetricsProcessor(MyProperties.getStr(PropertyKeys.CLASS_METRICS_PROCESSOR, PropertyValues.DEFAULT_CLASS_PROCESSOR));
            config.setGcMetricsProcessor(MyProperties.getStr(PropertyKeys.GC_METRICS_PROCESSOR, PropertyValues.DEFAULT_GC_PROCESSOR));
            config.setMemoryMetricsProcessor(MyProperties.getStr(PropertyKeys.MEM_METRICS_PROCESSOR, PropertyValues.DEFAULT_MEM_PROCESSOR));
            config.setThreadMetricsProcessor(MyProperties.getStr(PropertyKeys.THREAD_METRICS_PROCESSOR, PropertyValues.DEFAULT_THREAD_PROCESSOR));

            config.setRecorderMode(MyProperties.getStr(PropertyKeys.RECORDER_MODE, PropertyValues.RECORDER_MODE_ROUGH));
            config.setBackupRecorderCount(MyProperties.getInt(PropertyKeys.BACKUP_RECORDERS_COUNT, PropertyValues.MIN_BACKUP_RECORDERS_COUNT));
            config.setMilliTimeSlice(MyProperties.getLong(PropertyKeys.MILL_TIME_SLICE, PropertyValues.DEFAULT_TIME_SLICE));

            String includePackages = MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_PACKAGES, "");
            if (includePackages == null || includePackages.isEmpty()) {
                throw new IllegalArgumentException("IncludePackages is required!!!");
            }
            config.setIncludePackages(includePackages);

            config.setExcludePackages(MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_PACKAGES, ""));
            config.setPrintDebugLog(MyProperties.getBoolean(PropertyKeys.DEBUG_PRINT_DEBUG_LOG, false));
            config.setExcludeMethods(MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_METHODS, ""));
            config.setExcludePrivateMethod(MyProperties.getBoolean(PropertyKeys.EXCLUDE_PRIVATE_METHODS, true));
            config.setExcludeClassLoaders(MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_CLASS_LOADERS, ""));
            config.setProfilingParamsFile(MyProperties.getStr(PropertyKeys.PROFILING_PARAMS_FILE_NAME, ""));
            config.setCommonProfilingParams(MyProperties.getInt(PropertyKeys.PROFILING_TIME_THRESHOLD, 1000), MyProperties.getInt(PropertyKeys.PROFILING_OUT_THRESHOLD_COUNT, 16));
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
            ProfilingConfig config = ProfilingConfig.getInstance();
            MethodMetricsProcessor obj = createObj(config.getMethodMetricsProcessor());
            if (obj == null) {
                return false;
            }

            processor = AsyncMethodMetricsProcessor.initial(obj);
            return true;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Logger.error("AbstractBootstrap.initPerfStatsProcessor()", e);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> T createObj(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (className == null || className.isEmpty()) {
            Logger.error("AbstractBootstrap.createObj() className=" + className + " NOT FOUND!!!");
            return null;
        }

        Class<?> clazz = AbstractBootstrap.class.getClassLoader().loadClass(className);
        return (T) clazz.newInstance();
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
                        ExecutorServiceManager.stopAll(6, TimeUnit.SECONDS);
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
            List<Scheduler> schedulers = new ArrayList<>(2);
            schedulers.add(createJVMMetricsScheduler());
            schedulers.add(maintainer);

            LightWeightScheduler.initScheduleTask(schedulers, ProfilingConfig.getInstance().getMilliTimeSlice());

            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initScheduler()", e);
        }
        return false;
    }

    private Scheduler createJVMMetricsScheduler() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        ProfilingConfig config = ProfilingConfig.getInstance();
        JvmClassMetricsProcessor classProcessor = createObj(config.getClassMetricsProcessor());
        JvmGCMetricsProcessor gcProcessor = createObj(config.getGcMetricsProcessor());
        JvmMemoryMetricsProcessor memoryProcessor = createObj(config.getMemoryMetricsProcessor());
        JvmThreadMetricsProcessor threadProcessor = createObj(config.getThreadMetricsProcessor());
        return new JvmMetricsScheduler(classProcessor, gcProcessor, memoryProcessor, threadProcessor);
    }

    public abstract boolean initOther();

    private void printBannerText() {
        Logger.info("\n" +
                "    __  ___      ____            ______ __      __\n" +
                "   /  |/  /_  __/ __ \\___  _____/ __/ // /     / /\n" +
                "  / /|_/ / / / / /_/ / _ \\/ ___/ /_/ // /___  / / \n" +
                " / /  / / /_/ / ____/  __/ /  / __/__  __/ /_/ /  \n" +
                "/_/  /_/\\__, /_/    \\___/_/  /_/    /_/  \\____/   \n" +
                "       /____/                                     \n");
    }
}
