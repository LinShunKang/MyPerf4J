package cn.perf4j.test.aspectJ;

import cn.perf4j.AbstractRecorder;
import cn.perf4j.RoughRecorder;
import cn.perf4j.util.MapUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Map;

/**
 * Created by LinShunkang on 2018/4/6
 */
@Aspect
public class MethodStartAspect {

    private static ThreadLocal<Long> startTime = new ThreadLocal<>();

    private static long COUNTER = 0L;

    private static long startNanos = 0L;

    private static Map<String, AbstractRecorder> recorderMap = MapUtils.createHashMap(100, 0.2F);

    static {
        recorderMap.put("UserService.fetchUserById(..)", RoughRecorder.getInstance("UserService.fetchUserById(..)", 10000));
    }

    @Pointcut("@annotation(cn.perf4j.test.aspectJ.LogStartTime)")
    private void logStartTimePointcut() {

    }

    @Before("logStartTimePointcut()")
    public void before(JoinPoint joinPoint) {
//        startTime.set(System.currentTimeMillis());
//        System.out.println("MethodStartAspect.before()");
//        COUNTER += System.currentTimeMillis();
        startNanos = System.nanoTime();
    }

    @After("logStartTimePointcut()")
    public void after(JoinPoint joinPoint) {
//        startTime.set(System.currentTimeMillis());
//        System.out.println("MethodStartAspect.after: cost:" + (System.currentTimeMillis() - startTime.get()) + "ms");
//        System.out.println("MethodStartAspect.after()");
        AbstractRecorder recorder = recorderMap.get(joinPoint.getSignature().toShortString());
        if (recorder != null) {
            recorder.recordTime(startNanos, System.nanoTime());
        }
    }

    public static Long getStartTime() {
        return startTime.get();
    }

    public static void clearStartTime() {
        startTime.set(null);
    }

    public static AbstractRecorder getRecorder() {
        return recorderMap.get("UserService.fetchUserById(..)");
    }
}