package cn.myperf4j.core;

import cn.myperf4j.base.Version;
import cn.myperf4j.base.config.BasicConfig;
import cn.myperf4j.base.config.FilterConfig;
import cn.myperf4j.base.config.InfluxDbConfig;
import cn.myperf4j.base.config.LevelMappingFilter;
import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.config.RecorderConfig;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.constant.PropertyValues.Separator;
import cn.myperf4j.base.metric.exporter.JvmBufferPoolMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmClassMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmCompilationMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmFileDescMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmGcMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmMemoryMetricsExporter;
import cn.myperf4j.base.metric.exporter.JvmThreadMetricsExporter;
import cn.myperf4j.base.metric.exporter.MethodMetricsExporter;
import cn.myperf4j.base.metric.exporter.MetricsExporterFactory;
import cn.myperf4j.base.util.ExecutorManager;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.util.NumUtils;
import cn.myperf4j.base.util.StrUtils;
import cn.myperf4j.core.recorder.AbstractRecorderMaintainer;
import cn.myperf4j.core.scheduler.JvmMetricsScheduler;
import cn.myperf4j.base.Scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static cn.myperf4j.base.constant.PropertyKeys.Basic.PROPERTIES_FILE_DIR;
import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/4/11
 */
public abstract class AbstractBootstrap {

    private volatile boolean initStatus;

    protected MethodMetricsExporter processor;

    protected AbstractRecorderMaintainer maintainer;

    public final boolean initial() {
        try {
            if (initStatus) {
                Logger.warn("AbstractBootstrap is already init.");
                return true;
            }

            Logger.info("Thanks sincerely for using MyPerf4J.");
            if (!(initStatus = doInitial())) {
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

        if (!initMethodMetricsExporter()) {
            Logger.error("AbstractBootstrap initMethodMetricsExporter() FAILURE!!!");
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

            properties.put(PROPERTIES_FILE_DIR.key(), getConfigFileDir(configFilePath));
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
            ProfilingConfig.basicConfig(BasicConfig.loadBasicConfig());
            ProfilingConfig.metricsConfig(MetricsConfig.loadMetricsConfig());
            ProfilingConfig.influxDBConfig(InfluxDbConfig.loadInfluxDbConfig());
            ProfilingConfig.filterConfig(FilterConfig.loadFilterConfig());
            ProfilingConfig.recorderConfig(RecorderConfig.loadRecorderConfig());
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initProfilingConfig()", e);
        }
        return false;
    }

    private boolean initLogger() {
        try {
            Logger.setDebugEnable(ProfilingConfig.basicConfig().debug());
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initLogger()", e);
        }
        return false;
    }

    private boolean initPackageFilter() {
        try {
            FilterConfig filterConfig = ProfilingConfig.filterConfig();
            String includePackages = filterConfig.includePackages();
            List<String> includeList = StrUtils.splitAsList(includePackages, Separator.ELE);
            for (int i = 0; i < includeList.size(); i++) {
                ProfilingFilter.addIncludePackage(includeList.get(i));
            }

            String excludePackages = filterConfig.excludePackages();
            List<String> excludeList = StrUtils.splitAsList(excludePackages, Separator.ELE);
            for (int i = 0; i < excludeList.size(); i++) {
                ProfilingFilter.addExcludePackage(excludeList.get(i));
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initPackageFilter()", e);
        }
        return false;
    }

    private boolean initClassLoaderFilter() {
        try {
            FilterConfig filterConfig = ProfilingConfig.filterConfig();
            String excludeClassLoaders = filterConfig.excludeClassLoaders();
            List<String> excludeList = StrUtils.splitAsList(excludeClassLoaders, Separator.ELE);
            for (int i = 0; i < excludeList.size(); i++) {
                ProfilingFilter.addExcludeClassLoader(excludeList.get(i));
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initClassLoaderFilter()", e);
        }
        return false;
    }

    private boolean initMethodFilter() {
        try {
            FilterConfig filterConfig = ProfilingConfig.filterConfig();
            String excludeMethods = filterConfig.excludeMethods();
            List<String> excludeList = StrUtils.splitAsList(excludeMethods, Separator.ELE);
            for (int i = 0; i < excludeList.size(); i++) {
                ProfilingFilter.addExcludeMethods(excludeList.get(i));
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
            MetricsConfig metricsConfig = ProfilingConfig.metricsConfig();
            String levelMappings = metricsConfig.classLevelMapping();
            if (StrUtils.isBlank(levelMappings)) {
                Logger.info("ClassLevelMapping is blank, so use default mappings.");
                return true;
            }

            List<String> mappingPairs = StrUtils.splitAsList(levelMappings, Separator.ELE);
            for (int i = 0; i < mappingPairs.size(); ++i) {
                String mappingPair = mappingPairs.get(i);
                List<String> pairs = StrUtils.splitAsList(mappingPair, Separator.ELE_KV);
                if (pairs.size() != 2) {
                    Logger.warn("MethodLevelMapping is not correct: " + mappingPair);
                    continue;
                }

                LevelMappingFilter.putLevelMapping(pairs.get(0), getMappingExpList(pairs.get(1)));
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
        return StrUtils.splitAsList(expStr, Separator.ARR_ELE);
    }

    private boolean initMethodMetricsExporter() {
        try {
            String exporter = ProfilingConfig.metricsConfig().metricsExporter();
            processor = MetricsExporterFactory.getMethodMetricsExporter(exporter);
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initPerfStatsProcessor()", e);
        }
        return false;
    }

    private boolean initProfilingParams() {
        try {
            RecorderConfig recorderConf = ProfilingConfig.recorderConfig();
            if (recorderConf.accurateMode()) {
                addProfilingParams(recorderConf, ProfilingConfig.basicConfig().sysProfilingParamsFile());
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initProfilingParams()", e);
        }
        return false;
    }

    private void addProfilingParams(RecorderConfig recorderConf, String filePath) {
        File sysFile = new File(filePath);
        if (sysFile.exists() && sysFile.isFile()) {
            Logger.info("Loading " + sysFile.getName() + " to init profiling params.");
            addProfilingParams0(recorderConf, filePath);
        }
    }

    private void addProfilingParams0(RecorderConfig recorderConf, String profilingParamFile) {
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

                int timeThreshold = NumUtils.parseInt(strList.get(0).trim(), 1000);
                int outThresholdCount = NumUtils.parseInt(strList.get(1).trim(), 64);
                recorderConf.addProfilingParam(key.replace('.', '/'), timeThreshold, outThresholdCount);
            }
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.addProfilingParams(" + profilingParamFile + ")", e);
        }
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
            MetricsConfig metricsConfig = ProfilingConfig.metricsConfig();
            LightWeightScheduler.dispatchScheduleTask(maintainer, metricsConfig.methodMilliTimeSlice());
            LightWeightScheduler.dispatchScheduleTask(jvmMetricsScheduler(), metricsConfig.jvmMilliTimeSlice());
            LightWeightScheduler.dispatchScheduleTask(buildSysGenProfilingScheduler(), 60 * 1000); //1min
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initScheduler()", e);
        }
        return false;
    }

    private Scheduler jvmMetricsScheduler() {
        String exporter = ProfilingConfig.metricsConfig().metricsExporter();
        JvmClassMetricsExporter classExporter = MetricsExporterFactory.getClassMetricsExporter(exporter);
        JvmGcMetricsExporter gcExporter = MetricsExporterFactory.getGcMetricsExporter(exporter);
        JvmMemoryMetricsExporter memoryExporter = MetricsExporterFactory.getMemoryMetricsExporter(exporter);
        JvmBufferPoolMetricsExporter bufferPoolExporter = MetricsExporterFactory.getBufferPoolMetricsExporter(exporter);
        JvmThreadMetricsExporter threadExporter = MetricsExporterFactory.getThreadMetricsExporter(exporter);
        JvmCompilationMetricsExporter compilationExporter = MetricsExporterFactory.getCompilationExporter(exporter);
        JvmFileDescMetricsExporter fileDescExporter = MetricsExporterFactory.getFileDescExporter(exporter);
        return new JvmMetricsScheduler(
                classExporter,
                gcExporter,
                memoryExporter,
                bufferPoolExporter,
                threadExporter,
                compilationExporter,
                fileDescExporter
        );
    }

    private Scheduler buildSysGenProfilingScheduler() {
        return new Scheduler() {
            @Override
            public void run(long lastTimeSliceStartTime, long millTimeSlice) {
                RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
                if (bean.getUptime() >= 60 * 60 * 1000) { //60min
                    MethodMetricsHistogram.buildSysGenProfilingFile();
                }
            }

            @Override
            public String name() {
                return "ProfilingFileGenerator";
            }
        };
    }

    public abstract boolean initOther();

    private void printBannerText() {
        Logger.info(LINE_SEPARATOR +
                "    __  ___      ____            ______ __      __" + LINE_SEPARATOR +
                "   /  |/  /_  __/ __ \\___  _____/ __/ // /     / /" + LINE_SEPARATOR +
                "  / /|_/ / / / / /_/ / _ \\/ ___/ /_/ // /___  / / " + LINE_SEPARATOR +
                " / /  / / /_/ / ____/  __/ /  / __/__  __/ /_/ /  " + LINE_SEPARATOR +
                "/_/  /_/\\__, /_/    \\___/_/  /_/    /_/  \\____/   " + LINE_SEPARATOR +
                "       /____/                                     v" + Version.getVersion() + LINE_SEPARATOR);
    }
}
