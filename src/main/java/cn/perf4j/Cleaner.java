package cn.perf4j;

import cn.perf4j.aop.ProfilerContainer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class Cleaner implements InitializingBean {

    private ProfilerContainer profilerContainer;

    private AsyncRecordProcessor asyncRecordProcessor;


    public void setProfilerContainer(ProfilerContainer profilerContainer) {
        this.profilerContainer = profilerContainer;
    }

    public void setAsyncRecordProcessor(AsyncRecordProcessor asyncRecordProcessor) {
        this.asyncRecordProcessor = asyncRecordProcessor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(profilerContainer, "profilerContainer is required!!!");
        Assert.notNull(asyncRecordProcessor, "asyncRecordProcessor is required!!!");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("ENTER ProfilerContainer.shutdownHook...");
                    Map<String, AbstractRecorder> recorderMap = profilerContainer.getRecorderMap();
                    for (Map.Entry<String, AbstractRecorder> entry : recorderMap.entrySet()) {
                        AbstractRecorder recorder = entry.getValue();
                        recorder.setShutdown(true);
                    }

                    ThreadPoolExecutor executor = asyncRecordProcessor.getExecutor();
                    executor.shutdown();
                    executor.awaitTermination(30, TimeUnit.SECONDS);

                    for (Map.Entry<String, AbstractRecorder> entry : recorderMap.entrySet()) {
                        AbstractRecorder recorder = entry.getValue();
                        asyncRecordProcessor.process(recorder.getApi(), recorder.getStartMilliTime(), recorder.getStopMilliTime(), recorder.getSortedTimingRecords());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("EXIT ProfilerContainer.shutdownHook...");
                }
            }
        }));
    }
}
