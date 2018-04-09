package cn.perf4j.aop;

import cn.perf4j.AbstractRecorder;
import cn.perf4j.RecorderContainer;
import cn.perf4j.util.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;


/**
 * Created by LinShunkang on 2018/3/11
 */

/**
 * 该类利用切面对应用程序接口的响应时间进行统计
 * 为何只对拥有cn.perf4j.aop.Profiler注解的类和方法进行监控？
 * 1、内存更加可控，添加Profiler注解的人比程序更加了解接口的响应时间，可以更好的设置Profiler中的mostTimeThreshold和outThresholdCount。
 */
@Aspect
public class ProfilerAspect {

    private static final String JOIN_POINT_KIND_METHOD_EXE = "method-execution";

    @Around("(@within(cn.perf4j.aop.Profiler) || @annotation(cn.perf4j.aop.Profiler)) && !(@within(cn.perf4j.aop.NonProfiler) || @annotation(cn.perf4j.aop.NonProfiler))")
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
            AbstractRecorder recorder = RecorderContainer.getRecorder(api);
            if (recorder != null) {
                recorder.recordTime(startNano, System.nanoTime());
            } else {
//                Logger.error("ProfilerAspect.doProfile(): UnKnown api=" + api);
            }
        }
    }

    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
    private String getApi(ProceedingJoinPoint joinPoint) {
        if (joinPoint.getStaticPart() == null || !JOIN_POINT_KIND_METHOD_EXE.equals(joinPoint.getStaticPart().getKind())) {
            Logger.error("joinPoint.getStaticPart().getKind(): " + joinPoint.getStaticPart().getKind());
            return "";
        }

        Class<?> clazz = joinPoint.getTarget().getClass();
        return clazz.getSimpleName().concat(".").concat(joinPoint.getSignature().getName());
    }

}
