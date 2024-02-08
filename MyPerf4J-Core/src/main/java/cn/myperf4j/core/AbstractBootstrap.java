package cn.myperf4j.core;

import cn.myperf4j.base.Version;
import cn.myperf4j.base.config.FilterConfig;
import cn.myperf4j.base.config.HttpServerConfig;
import cn.myperf4j.base.config.LevelMappingFilter;
import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.config.RecorderConfig;
import cn.myperf4j.base.constant.PropertyValues.Separator;
import cn.myperf4j.base.http.HttpHeaders;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.http.server.Dispatcher;
import cn.myperf4j.base.http.server.SimpleHttpServer;
import cn.myperf4j.base.metric.exporter.MethodMetricsExporter;
import cn.myperf4j.base.util.concurrent.ExecutorManager;
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

import static cn.myperf4j.base.config.BasicConfig.loadBasicConfig;
import static cn.myperf4j.base.config.FilterConfig.loadFilterConfig;
import static cn.myperf4j.base.config.HttpServerConfig.loadHttpServerConfig;
import static cn.myperf4j.base.config.InfluxDbConfig.loadInfluxDbConfig;
import static cn.myperf4j.base.config.MetricsConfig.loadMetricsConfig;
import static cn.myperf4j.base.config.RecorderConfig.loadRecorderConfig;
import static cn.myperf4j.base.constant.PropertyKeys.Basic.PROPERTIES_FILE_DIR;
import static cn.myperf4j.base.constant.PropertyKeys.PRO_FILE_NAME;
import static cn.myperf4j.base.constant.PropertyValues.DEFAULT_PRO_FILE;
import static cn.myperf4j.base.constant.PropertyValues.Separator.ELE;
import static cn.myperf4j.base.constant.PropertyValues.Separator.ELE_KV;
import static cn.myperf4j.base.http.HttpRespStatus.NOT_FOUND;
import static cn.myperf4j.base.http.HttpRespStatus.OK;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getBufferPoolMetricsExporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getClassMetricsExporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getCompilationExporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getFileDescExporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getGcMetricsExporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getGcMetricsV3Exporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getMemoryMetricsExporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getMethodMetricsExporter;
import static cn.myperf4j.base.metric.exporter.MetricsExporterFactory.getThreadMetricsExporter;
import static cn.myperf4j.base.util.net.NetUtils.isPortAvailable;
import static cn.myperf4j.base.util.StrUtils.splitAsList;
import static cn.myperf4j.base.util.SysProperties.LINE_SEPARATOR;

/**
 * Created by LinShunkang on 2018/4/11
 */
public abstract class AbstractBootstrap {

    private volatile boolean initStatus;

    protected MethodMetricsExporter methodMetricsExporter;

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

        if (!initHttpServer()) {
            Logger.error("AbstractBootstrap initHttpServer() FAILURE!!!");
            return false;
        }

        if (!initOther()) {
            Logger.error("AbstractBootstrap initOther() FAILURE!!!");
            return false;
        }
        return true;
    }

    private boolean initProperties() {
        final String configFilePath = System.getProperty(PRO_FILE_NAME, DEFAULT_PRO_FILE);
        try (InputStream in = new FileInputStream(configFilePath)) {
            Properties properties = new Properties();
            properties.load(in);

            properties.put(PROPERTIES_FILE_DIR.key(), parseConfigFileDir(configFilePath));
            return MyProperties.initial(properties);
        } catch (IOException e) {
            Logger.error("AbstractBootstrap.initProperties()", e);
        }
        return false;
    }

    private String parseConfigFileDir(String configFilePath) {
        final int idx = configFilePath.lastIndexOf(File.separatorChar);
        return configFilePath.substring(0, idx + 1);
    }

    private boolean initProfilingConfig() {
        try {
            ProfilingConfig.basicConfig(loadBasicConfig());
            ProfilingConfig.metricsConfig(loadMetricsConfig());
            ProfilingConfig.influxDBConfig(loadInfluxDbConfig());
            ProfilingConfig.filterConfig(loadFilterConfig());
            ProfilingConfig.recorderConfig(loadRecorderConfig());
            ProfilingConfig.httpServerConfig(loadHttpServerConfig());
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
            final FilterConfig filterConfig = ProfilingConfig.filterConfig();
            final String includePackages = filterConfig.includePackages();
            final List<String> includeList = splitAsList(includePackages, ELE);
            for (int i = 0; i < includeList.size(); i++) {
                ProfilingFilter.addIncludePackage(includeList.get(i));
            }

            final String excludePackages = filterConfig.excludePackages();
            final List<String> excludeList = splitAsList(excludePackages, ELE);
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
            final FilterConfig filterConfig = ProfilingConfig.filterConfig();
            final String excludeClassLoaders = filterConfig.excludeClassLoaders();
            final List<String> excludeList = splitAsList(excludeClassLoaders, ELE);
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
            final FilterConfig filterConfig = ProfilingConfig.filterConfig();
            final String excludeMethods = filterConfig.excludeMethods();
            final List<String> excludeList = splitAsList(excludeMethods, ELE);
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
            final MetricsConfig metricsConfig = ProfilingConfig.metricsConfig();
            final String levelMappings = metricsConfig.classLevelMapping();
            if (StrUtils.isBlank(levelMappings)) {
                Logger.info("ClassLevelMapping is blank, so use default mappings.");
                return true;
            }

            final List<String> mappingPairs = splitAsList(levelMappings, ELE);
            for (int i = 0; i < mappingPairs.size(); ++i) {
                final String mappingPair = mappingPairs.get(i);
                final List<String> pairs = splitAsList(mappingPair, ELE_KV);
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
        return splitAsList(expStr, Separator.ARR_ELE);
    }

    private boolean initMethodMetricsExporter() {
        try {
            final String exporter = ProfilingConfig.metricsConfig().metricsExporter();
            methodMetricsExporter = getMethodMetricsExporter(exporter);
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initPerfStatsProcessor()", e);
        }
        return false;
    }

    private boolean initProfilingParams() {
        try {
            final RecorderConfig recorderConf = ProfilingConfig.recorderConfig();
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
        final File sysFile = new File(filePath);
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

                List<String> strList = splitAsList(value, ':');
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
            final MetricsConfig config = ProfilingConfig.metricsConfig();
            LightWeightScheduler.dispatchScheduleTask(maintainer, config.methodMilliTimeSlice());
            LightWeightScheduler.dispatchScheduleTask(buildJvmMetricsScheduler(), config.jvmMilliTimeSlice());
            LightWeightScheduler.dispatchScheduleTask(buildSysGenProfilingScheduler(), 60 * 1000); //1min
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initScheduler()", e);
        }
        return false;
    }

    private Scheduler buildJvmMetricsScheduler() {
        final String exporter = ProfilingConfig.metricsConfig().metricsExporter();
        return new JvmMetricsScheduler(
                getClassMetricsExporter(exporter),
                getGcMetricsExporter(exporter),
                getGcMetricsV3Exporter(exporter),
                getMemoryMetricsExporter(exporter),
                getBufferPoolMetricsExporter(exporter),
                getThreadMetricsExporter(exporter),
                getCompilationExporter(exporter),
                getFileDescExporter(exporter)
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

    private boolean initHttpServer() {
        try {
            final HttpServerConfig config = ProfilingConfig.httpServerConfig();
            final SimpleHttpServer server = new SimpleHttpServer.Builder()
                    .port(choseHttpServerPort(config))
                    .minWorkers(config.getMinWorkers())
                    .maxWorkers(config.getMaxWorkers())
                    .acceptCnt(config.getAcceptCount())
                    .dispatcher(getHttpServerDispatch())
                    .build();
            server.startAsync();
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initHttpServer()", e);
        }
        return false;
    }

    private int choseHttpServerPort(final HttpServerConfig config) {
        final int preferencePort = config.getPreferencePort();
        if (isPortAvailable(preferencePort)) {
            Logger.info("Use " + preferencePort + " as HttpServer port.");
            return preferencePort;
        }

        for (int port = config.getMinPort(); port < config.getMaxPort(); port++) {
            if (isPortAvailable(port)) {
                Logger.info("Use " + port + " as HttpServer port.");
                return port;
            }
        }
        throw new IllegalStateException("Has no available port for HttpServer!");
    }

    private Dispatcher getHttpServerDispatch() {
        return new Dispatcher() {
            @Override
            public HttpResponse dispatch(HttpRequest request) {
                switch (request.getPath()) {
                    case "/switch/debugMode":
                        Logger.setDebugEnable(request.getBoolParam("enable"));
                        break;
                    default:
                        return new HttpResponse(NOT_FOUND, new HttpHeaders(0), "");
                }
                return new HttpResponse(OK, new HttpHeaders(0), "Success");
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
