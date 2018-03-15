package cn.perf4j.aop;

import cn.perf4j.AbstractTimingRecorder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by LinShunkang on 2018/3/11
 */
@Aspect
@Component
public class ProfilerAspect {

    @Autowired
    private ProfilerContainer profilerContainer;

    //    @Pointcut("execution(public * cn.perf4j.*.impl.*.* (..))")//OK!!!
    @Pointcut("@within(cn.perf4j.aop.Profiler) || @annotation(cn.perf4j.aop.Profiler)")//OK!!!
    private void profileMethod() {
    }

    @Around("profileMethod()")//OK!!!
    public Object doProfile(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNano = System.nanoTime();
        String api = "";
        try {
            Class<?> clazz = joinPoint.getTarget().getClass();
            api = clazz.getSimpleName() + "." + joinPoint.getSignature().getName();
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            System.err.println("环绕增强发生异常!!!");
            throw throwable;
        } finally {
            AbstractTimingRecorder recorder = profilerContainer.getRecorder(api);
            if (recorder == null) {
                System.err.println("ProfilerAspect.doProfile(): UnKnown api=" + api);
            } else {
                recorder.recordTime(startNano, System.nanoTime());
            }
        }
    }
}
