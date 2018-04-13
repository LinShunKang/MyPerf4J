package cn.perf4j.aop;

import cn.perf4j.AbstractRecorder;
import cn.perf4j.RecorderMaintainer;
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

    private static RecorderMaintainer recorderMaintainer;

    private static boolean running = false;

    static {
        try {
            Class.forName("cn.perf4j.MyBootstrap");
        } catch (Throwable e) {
            Logger.error("ProfilerAspect: loadClass cn.perf4j.MyBootstrap failure!!!", e);
        }
    }

    @Around("(@within(cn.perf4j.aop.Profiler) || @annotation(cn.perf4j.aop.Profiler)) && !(@within(cn.perf4j.aop.NonProfiler) || @annotation(cn.perf4j.aop.NonProfiler))")
    public Object doProfiling(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNano = running ? System.nanoTime() : 0L;
        String tag = null;
        try {
            tag = getTag(joinPoint);
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (Throwable throwable) {
            Logger.error("ProfilerAspect.doProfiling() tag: " + tag, throwable);
            throw throwable;
        } finally {
            AbstractRecorder recorder;
            if (running && (recorder = recorderMaintainer.getRecorder(tag)) != null) {
                recorder.recordTime(startNano, System.nanoTime());
            } else if (running) {
                Logger.warn("ProfilerAspect.doProfile(): UNKNOWN tag: " + tag);
            }
        }
    }

    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
    private String getTag(ProceedingJoinPoint joinPoint) {
        if (joinPoint.getStaticPart() == null || !JOIN_POINT_KIND_METHOD_EXE.equals(joinPoint.getStaticPart().getKind())) {
            Logger.warn("joinPoint.getStaticPart().getKind(): " + joinPoint.getStaticPart());
            return "";
        }

        Class<?> clazz = joinPoint.getTarget().getClass();
        return clazz.getSimpleName().concat(".").concat(joinPoint.getSignature().getName());
    }


    public static void setRecorderMaintainer(RecorderMaintainer recorderMaintainer) {
        ProfilerAspect.recorderMaintainer = recorderMaintainer;
    }

    public static void setRunning(boolean running) {
        ProfilerAspect.running = running;
    }
}
