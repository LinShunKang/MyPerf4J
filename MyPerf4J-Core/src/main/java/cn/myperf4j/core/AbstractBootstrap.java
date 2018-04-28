package cn.myperf4j.core;


import cn.myperf4j.core.constant.PropertyKeys;
import cn.myperf4j.core.constant.PropertyValues;
import cn.myperf4j.core.util.IOUtils;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.MyProperties;
import cn.myperf4j.core.util.PerfStatsCalculator;

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

        if (!initLogger()) {
            Logger.error("AbstractBootstrap initLogger() FAILURE!!!");
            return false;
        }

        if (!initPackageFilter()) {
            Logger.error("AbstractBootstrap initPackageFilter() FAILURE!!!");
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
            in = AsyncPerfStatsProcessor.class.getClassLoader().getResourceAsStream(PropertyKeys.PRO_FILE_NAME);
            if (in == null) {
                return false;
            }

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

    private boolean initLogger() {
        try {
            boolean debug = MyProperties.getBoolean(PropertyKeys.LOG_DEBUG, false);
            Logger.setDebugEnable(debug);
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initLogger()", e);
        }
        return false;
    }

    private boolean initPackageFilter() {
        try {
            String includePackages = MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_PACKAGES, "");
            String[] includeArr = includePackages.split(PropertyValues.FILTER_PACKAGES_SPLIT);
            if (includeArr.length > 0) {
                for (String pkg : includeArr) {
                    ProfilerFilter.addNeedInjectPackage(pkg);
                }
            }

            String excludePackages = MyProperties.getStr(PropertyKeys.FILTER_EXCLUDE_PACKAGES, "");
            String[] excludeArr = excludePackages.split(PropertyValues.FILTER_PACKAGES_SPLIT);
            if (excludeArr.length > 0) {
                for (String pkg : excludeArr) {
                    ProfilerFilter.addNotNeedInjectPackage(pkg);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.error("AbstractBootstrap.initPackageFilter()", e);
        }
        return false;
    }

    private boolean initPerfStatsProcessor() {
        try {
            String className = MyProperties.getStr(PropertyKeys.PERF_STATS_PROCESSOR);
            if (className == null || className.isEmpty()) {
                Logger.error("AbstractBootstrap.initPerfStatsProcessor() MyPerf4J.PSP NOT FOUND!!!");
                return false;
            }

            Class<?> clazz = AsyncPerfStatsProcessor.class.getClassLoader().loadClass(className);
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
