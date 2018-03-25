package cn.perf4j.aop;

import cn.perf4j.AbstractRecorder;
import cn.perf4j.RecorderContainer;
import cn.perf4j.util.Logger;
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
 * 1、内存更加可控，添加Profiler注解的人比程序更加了解接口的响应时间，可以更好的设置Profiler中的mostTimeThreshold和outThresholdCount。
 */
@Aspect
public class ProfilerAspect implements InitializingBean/*, MethodInterceptor*/ {

    private RecorderContainer recorderContainer;

    @Around("@within(cn.perf4j.aop.Profiler) || @annotation(cn.perf4j.aop.Profiler)")
    public Object doProfiling(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNano = System.nanoTime();
        String api = null;
        try {
            api = getApi(joinPoint);
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            Logger.error("ProfilerAspect.doProfiling(): api=" + api, throwable);
            throw throwable;
        } finally {
            AbstractRecorder recorder = recorderContainer.getRecorder(api);
            if (recorder != null) {
                recorder.recordTime(startNano, System.nanoTime());
            } else {
                Logger.error("ProfilerAspect.doProfile(): UnKnown api=" + api);
            }
        }
    }

    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
    private String getApi(ProceedingJoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        return clazz.getSimpleName().concat(".").concat(joinPoint.getSignature().getName());
    }

//
//    @Override
//    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
//        long startNano = System.nanoTime();
//        String api = null;
//        try {
//            api = getApi(methodInvocation);
//            return methodInvocation.proceed();
//        } catch (Throwable throwable) {
//            System.err.println("ProfilerAspect.invoke(): api=" + api);
//            throw throwable;
//        } finally {
//            AbstractRecorder recorder = recorderContainer.getRecorder(api);
//            if (recorder != null) {
//                recorder.recordTime(startNano, System.nanoTime());
//            } else {
//                System.err.println("ProfilerAspect.invoke(): UnKnown api=" + api);
//            }
//        }
//    }
//
//    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
//    private String getApi(MethodInvocation methodInvocation) {
//        Class<?> clazz = methodInvocation.getThis().getClass();
//        return clazz.getSimpleName().concat(".").concat(methodInvocation.getMethod().getName());
//    }

    public void setRecorderContainer(RecorderContainer recorderContainer) {
        this.recorderContainer = recorderContainer;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(recorderContainer, "recorderContainer is required!!!");
    }
}
