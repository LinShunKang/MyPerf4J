package cn.perf4j;

import cn.perf4j.util.AopTargetUtils;
import cn.perf4j.aop.Profiler;
import cn.perf4j.util.MapUtils;
import cn.perf4j.util.ThreadUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/13
 */

/**
 * 该类用于存储所有添加了注解的接口对应的Recorder
 * 该类的实现原因：
 * 1.为了保证运行时的性能和简化编程，在程序启动时就把recorderMap和backupRecorderMap初始化完成，并且通过减小loadFactor来优化Map的get()性能；
 * 2.为了避免影响程序的响应时间，利用roundRobinExecutor定时去对recorderMap和backupRecorderMap进行轮转；
 * 3.为了避免recordProcessor处理时间过长影响roundRobinExecutor的处理逻辑，增加一个backgroundExecutor来定时执行recordProcessor
 */
public class RecorderContainer implements InitializingBean, ApplicationContextAware {

    private static final long millTimeSlice = 60 * 1000L;//60s
//    private static final long millTimeSlice = 10 * 1000L;//10s

    private static final ScheduledThreadPoolExecutor roundRobinExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtils.newThreadFactory("MyPerf4J-RoundRobinExecutor_"), new ThreadPoolExecutor.DiscardPolicy());

    private static final ScheduledThreadPoolExecutor backgroundExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtils.newThreadFactory("MyPerf4J-BackgroundExecutor_"), new ThreadPoolExecutor.DiscardPolicy());

    private volatile long nextMilliTimeSlice = ((System.currentTimeMillis() / millTimeSlice) * millTimeSlice) + millTimeSlice;

    private volatile boolean backupRecorderReady = false;

    private ApplicationContext applicationContext;

    private PerfStatsProcessor perfStatsProcessor;

    //为了让recorderMap.get()更加快速，减小loadFactor->减少碰撞的概率->加快get()的执行速度
    private volatile Map<String, AbstractRecorder> recorderMap = MapUtils.createHashMap(1000, 0.4F);

    private volatile Map<String, AbstractRecorder> backupRecorderMap = MapUtils.createHashMap(1000, 0.6F);

    public AbstractRecorder getRecorder(String api) {
        return recorderMap.get(api);
    }

    public Map<String, AbstractRecorder> getRecorderMap() {
        return new HashMap<>(recorderMap);
    }

    private void initRecorderMap() {
        if (applicationContext == null) {
            System.err.println("RecorderContainer.initRecorderMap(): applicationContext is null!!!");
            return;
        }

        long startMills = System.currentTimeMillis();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (int i = 0; i < beanNames.length; ++i) {
            try {
                Object bean = applicationContext.getBean(beanNames[i]);
                if (AopUtils.isAopProxy(bean)) {
                    bean = AopTargetUtils.getTarget(bean);
                }

                Class<?> clazz = bean.getClass();
                Profiler classProfiler = clazz.getAnnotation(Profiler.class);
                Method[] methodArray = clazz.getMethods();
                for (int k = 0, length = methodArray.length; k < length; ++k) {
                    Method method = methodArray[k];
                    if (!clazz.equals(method.getDeclaringClass())) {
                        continue;
                    }

                    Profiler methodProfiler = AnnotationUtils.findAnnotation(method, Profiler.class);
                    if (methodProfiler == null) {
                        methodProfiler = classProfiler;
                    }
                    if (methodProfiler == null) {
                        continue;
                    }

                    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
                    String api = clazz.getSimpleName() + "." + method.getName();
                    recorderMap.put(api, Recorder.getInstance(api, methodProfiler.mostTimeThreshold(), methodProfiler.outThresholdCount()));
                    backupRecorderMap.put(api, Recorder.getInstance(api, methodProfiler.mostTimeThreshold(), methodProfiler.outThresholdCount()));
                }
            } catch (Exception e) {
                System.err.println("RecorderContainer.initRecorderMap(): init Error!!!");
            }
        }
        System.out.println("RecorderContainer.initRecorderMap() cost:" + (System.currentTimeMillis() - startMills) + "ms");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setPerfStatsProcessor(PerfStatsProcessor perfStatsProcessor) {
        this.perfStatsProcessor = perfStatsProcessor;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(applicationContext, "applicationContext is required!!!");
        Assert.notNull(perfStatsProcessor, "perfStatsProcessor is required!!!");

        initRecorderMap();

        roundRobinExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long currentMills = System.currentTimeMillis();
                if (nextMilliTimeSlice == 0L) {
                    nextMilliTimeSlice = ((currentMills / millTimeSlice) * millTimeSlice) + millTimeSlice;
                }

                //还在当前的时间片里
                if (nextMilliTimeSlice > currentMills) {
                    return;
                }
                nextMilliTimeSlice = ((currentMills / millTimeSlice) * millTimeSlice) + millTimeSlice;

                backupRecorderReady = false;
                try {
                    for (Map.Entry<String, AbstractRecorder> entry : recorderMap.entrySet()) {
                        AbstractRecorder curRecorder = entry.getValue();
                        if (curRecorder.getStartMilliTime() <= 0L || curRecorder.getStopMilliTime() <= 0L) {
                            curRecorder.setStartMilliTime(currentMills - millTimeSlice);
                            curRecorder.setStopMilliTime(currentMills);
                        }
                    }

                    for (Map.Entry<String, AbstractRecorder> entry : backupRecorderMap.entrySet()) {
                        AbstractRecorder backupRecorder = entry.getValue();
                        backupRecorder.resetRecord();
                        backupRecorder.setStartMilliTime(currentMills);
                        backupRecorder.setStopMilliTime(currentMills + millTimeSlice);
                    }

                    Map<String, AbstractRecorder> tmpMap = recorderMap;
                    recorderMap = backupRecorderMap;
                    backupRecorderMap = tmpMap;
                    System.out.println("Time=" + new Date(currentMills) + ", roundRobinExecutor finished!!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    backupRecorderReady = true;
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);

        backgroundExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!backupRecorderReady) {
                    return;
                }

                try {
                    AbstractRecorder recorder = null;
                    List<PerfStats> perfStatsList = new ArrayList<>(backupRecorderMap.size());
                    for (Map.Entry<String, AbstractRecorder> entry : backupRecorderMap.entrySet()) {
                        recorder = entry.getValue();
                        perfStatsList.add(PerfStatsCalculator.calPerfStats(recorder));
                    }

                    if (recorder != null) {
                        perfStatsProcessor.process(perfStatsList, recorder.getStartMilliTime(), recorder.getStopMilliTime());
                    }

                    System.out.println("Time=" + new Date() + ", backgroundExecutor finished!!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    backupRecorderReady = false;
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
