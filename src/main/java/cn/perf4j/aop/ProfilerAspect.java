package cn.perf4j.aop;

import cn.perf4j.AbstractRecorder;
import cn.perf4j.RecorderContainer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


/**
 * Created by LinShunkang on 2018/3/11
 */

/**
 * 该类利用切面对应用程序接口的响应时间进行统计
 * 为何只对拥有cn.perf4j.aop.Profiler注解的类和方法进行监控？
 * 因为为了保证运行时的性能，会在recorderContainer初始化的时候就把所有api对应的Recorder初始化完成，
 * 保证所有执行到doProfiling()方法的接口一定有对应的AbstractRecorder，而不用先判断不存在然后再插入，避免同步，简化代码逻辑。
 */
@Aspect
public class ProfilerAspect implements InitializingBean {

    private RecorderContainer recorderContainer;

    @Around("@within(cn.perf4j.aop.Profiler) || @annotation(cn.perf4j.aop.Profiler)")
    public Object doProfiling(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNano = System.nanoTime();
        String api = null;
        try {
            api = getApi(joinPoint);
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            System.err.println("ProfilerAspect.doProfiling(): api=" + api);
            throw throwable;
        } finally {
            AbstractRecorder recorder = recorderContainer.getRecorder(api);
            if (recorder != null) {
                recorder.recordTime(startNano, System.nanoTime());
            } else {
                System.err.println("ProfilerAspect.doProfile(): UnKnown api=" + api);
            }
        }
    }

    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
    private String getApi(ProceedingJoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        return clazz.getSimpleName().concat(".").concat(joinPoint.getSignature().getName());
    }

    public void setRecorderContainer(RecorderContainer recorderContainer) {
        this.recorderContainer = recorderContainer;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(recorderContainer, "recorderContainer is required!!!");
    }
}
