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
import java.util.Map;

/**
 * Created by LinShunkang on 2018/3/13
 */
@Component
public class ProfilerContainer implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, AbstractTimingRecorder> recorderMap = MapUtils.createHashMap(100);


    public AbstractTimingRecorder getRecorder(String api) {
        return recorderMap.get(api);
    }

    public Map<String, AbstractTimingRecorder> getRecorderMap() {
        return recorderMap;
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
                for (Method method : methodArray) {
                    if (!clazz.equals(method.getDeclaringClass())) {
                        continue;
                    }

                    Profiler methodProfiler = AnnotationUtils.findAnnotation(method, Profiler.class);
                    if (methodProfiler == null) {
                        methodProfiler = classProfiler;
                    }

                    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
                    String api = clazz.getSimpleName() + "." + method.getName();
                    AbstractTimingRecorder recorder = CycleTimingRecorder.getInstance(api, methodProfiler.mostTimeThreshold(), methodProfiler.outThresholdCount(), new RecorderProcessor() {
                        @Override
                        public void process(AbstractTimingRecorder recorder) {
//                            System.out.println(recorder.getSortedTimingRecords());
                            System.out.println(PerfCalculator.calPerfStat(recorder));
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
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(applicationContext, "applicationContext is required!!!");
        initRecorderMap();
        System.out.println(recorderMap);
    }

}
