package cn.perf4j;

import cn.perf4j.aop.AopTargetUtils;
import cn.perf4j.aop.Profiler;
import cn.perf4j.utils.MapUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LinShunkang on 2018/3/13
 */
public class RecorderContainer implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private RecordProcessor recordProcessor;

    //为了让recorderMap.get()更加快速，减小loadFactor->减少碰撞的概率->加快get()的执行速度
    private final Map<String, AbstractRecorder> recorderMap = MapUtils.createHashMap(1000, 0.4F);

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
                    recorderMap.put(api, RoundRobinRecorder.getInstance(api, methodProfiler.mostTimeThreshold(), methodProfiler.outThresholdCount(), recordProcessor));
                }
            } catch (Exception e) {
                System.err.println("RecorderContainer.initRecorderMap(): init Error!!!");
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setRecordProcessor(RecordProcessor recordProcessor) {
        this.recordProcessor = recordProcessor;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(applicationContext, "applicationContext is required!!!");
        Assert.notNull(recordProcessor, "recordProcessor is required!!!");

        initRecorderMap();
        System.out.println(recorderMap);
    }
}
