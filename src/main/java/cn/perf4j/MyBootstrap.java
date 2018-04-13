package cn.perf4j;

import cn.perf4j.aop.ProfilerAspect;
import cn.perf4j.util.IOUtils;
import cn.perf4j.util.Logger;
import cn.perf4j.util.MyProperties;
import cn.perf4j.util.PerfStatsCalculator;

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
public class MyBootstrap {

    private static AsyncPerfStatsProcessor processor;

    private static RecorderMaintainer maintainer;

    static {
        if (!initial()) {
            MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_NO);
            Logger.error("MyBootstrap initial failure!!!");
        } else {
            MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_YES);
            Logger.info("MyBootstrap initial success!!!");
        }
    }

    private static boolean initial() {
        try {
            if (!initMyProperties()) {
                return false;
            }

            if (!initPerfStatsProcessor()) {
                return false;
            }

            if (!initRecorderMaintainer()) {
                return false;
            }

            if (!initShutDownHook()) {
                return false;
            }

            if (!initProfilerAspect()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            Logger.error("MyBootstrap.initial()", e);
        }
        return false;
    }

    private static boolean initMyProperties() {
        InputStream in = null;
        try {
            in = AsyncPerfStatsProcessor.class.getClassLoader().getResourceAsStream(PropConstants.PRO_FILE_NAME);
            if (in == null) {
                return false;
            }

            Properties properties = new Properties();
            properties.load(in);
            return MyProperties.initial(properties);
        } catch (IOException e) {
            Logger.error("MyBootstrap.initMyProperties()", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return false;
    }

    private static boolean initPerfStatsProcessor() {
        try {
            String className = MyProperties.getStr(PropConstants.PERF_STATS_PROCESSOR);
            if (className == null || className.isEmpty()) {
                Logger.error("MyPerf4J.PSP NOT FOUND!!!");
                return false;
            }

            Class<?> clazz = AsyncPerfStatsProcessor.class.getClassLoader().loadClass(className);
            Object obj = clazz.newInstance();
            if (!(obj instanceof PerfStatsProcessor)) {
                Logger.error("MyBootstrap.initPerfStatsProcessor() className is not correct!!!");
                return false;
            }

            processor = AsyncPerfStatsProcessor.initial((PerfStatsProcessor) obj);
            return true;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Logger.error("MyBootstrap.initPerfStatsProcessor()", e);
        }
        return false;
    }

    private static boolean initRecorderMaintainer() {
        try {
            boolean accurateMode = MyProperties.isSame(PropConstants.RECORDER_MODE, PropConstants.RECORDER_MODE_ACCURATE);
            long milliTimeSlice = MyProperties.getLong(PropConstants.MILL_TIME_SLICE, PropConstants.DEFAULT_TIME_SLICE);

            maintainer = RecorderMaintainer.initial(processor, accurateMode, milliTimeSlice);
            return true;
        } catch (Exception e) {
            Logger.error("MyBootstrap.initRecorderMaintainer()", e);
        }
        return false;
    }

    private static boolean initShutDownHook() {
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
            Logger.error("MyBootstrap.initShutDownHook()", e);
        }
        return false;
    }

    private static boolean initProfilerAspect() {
        try {
            ProfilerAspect.setRecorderMaintainer(maintainer);
            ProfilerAspect.setRunning(true);
            return true;
        } catch (Exception e) {
            Logger.error("MyBootstrap.initProfilerAspect()", e);
        }
        return false;
    }

}
