package cn.perf4j.aop;

import cn.perf4j.*;
import cn.perf4j.utils.MapUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LinShunkang on 2018/3/13
 */
@Component
public class ProfilerContainer implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    //为了让recorderMap.get()更加快速，减小loadFactor->减少碰撞的概率->加快get()的执行速度
    private final Map<String, AbstractTimingRecorder> recorderMap = MapUtils.createHashMap(1000, 0.4F);

    public AbstractTimingRecorder getRecorder(String api) {
        return recorderMap.get(api);
    }

    public Map<String, AbstractTimingRecorder> getRecorderMap() {
        return new HashMap<>(recorderMap);
    }

    private void initRecorderMap() {
        if (applicationContext == null) {
            System.err.println("ProfilerContainer.initRecorderMap(): applicationContext is null!!!");
            return;
        }

        Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(Profiler.class);
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            try {
                Object bean = entry.getValue();
                if (AopUtils.isAopProxy(bean)) {
                    bean = AopTargetUtils.getTarget(bean);
                }

                Class<?> clazz = bean.getClass();
                Profiler classProfiler = clazz.getAnnotation(Profiler.class);
                Method[] methodArray = clazz.getMethods();
                for (int i = 0, length = methodArray.length; i < length; ++i) {
                    Method method = methodArray[i];
                    if (!clazz.equals(method.getDeclaringClass())) {
                        continue;
                    }

                    Profiler methodProfiler = AnnotationUtils.findAnnotation(method, Profiler.class);
                    if (methodProfiler == null) {
                        methodProfiler = classProfiler;
                    }

                    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
                    String api = clazz.getSimpleName() + "." + method.getName();
                    AbstractTimingRecorder recorder = RoundRobinTimingRecorder.getInstance(api, methodProfiler.mostTimeThreshold(), methodProfiler.outThresholdCount(), new RecorderProcessor() {
                        @Override
                        public void process(String api, long startMilliTime, long stopMillTime, List<TimingRecord> sortedRecords) {
                            System.out.println(PerfStatsCalculator.calPerfStat(api, startMilliTime, stopMillTime, sortedRecords));
                        }
                    });
                    recorderMap.put(api, recorder);
                }
            } catch (Exception e) {
                System.err.println("ProfilerContainer.initRecorderMap(): init Error!!!");
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(applicationContext, "applicationContext is required!!!");
        initRecorderMap();
        System.out.println(recorderMap);
    }

}
