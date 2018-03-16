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
@Aspect
public class ProfilerAspect implements InitializingBean {

    private RecorderContainer recorderContainer;

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
        return clazz.getSimpleName() + "." + joinPoint.getSignature().getName();
    }

    public void setRecorderContainer(RecorderContainer recorderContainer) {
        this.recorderContainer = recorderContainer;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(recorderContainer, "recorderContainer is required!!!");
    }
}
