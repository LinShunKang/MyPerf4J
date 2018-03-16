package cn.perf4j.aop;

import cn.perf4j.AbstractRecorder;
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
    private void profilingMethod() {
    }

    @Around("profilingMethod()")//OK!!!
    public Object doProfiling(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNano = System.nanoTime();
        String api = "";
        try {
            api = getApi(joinPoint);
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            System.err.println("环绕增强发生异常!!!");
            throw throwable;
        } finally {
            AbstractRecorder recorder = profilerContainer.getRecorder(api);
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
        return clazz.getSimpleName() + "." + joinPoint.getSignature().getName();
    }
}
