package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.core.MethodTagMaintainer;
import cn.myperf4j.core.recorder.Recorder;

import java.lang.reflect.Method;

/**
 * Created by LinShunkang on 2018/4/15
 */
public final class ProfilingAspect {

    private static final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private static ASMRecorderMaintainer recorderMaintainer;

    private static boolean running;

    private ProfilingAspect() {
        //empty
    }

    public static void profiling(final long startNanos, final int methodTagId) {
        try {
            final Recorder recorder;
            if (running && (recorder = recorderMaintainer.getRecorder(methodTagId)) != null) {
                recorder.recordTime(startNanos, System.nanoTime());
            }
        } catch (Throwable t) {
            Logger.error("ProfilingAspect.profiling(" + startNanos + ", " + methodTagId + ", "
                    + MethodTagMaintainer.getInstance().getMethodTag(methodTagId) + ")", t);
        }
    }

    //InvocationHandler.invoke(Object proxy, Method method, Object[] args)
    public static void profiling(final long startNanos, final Method method) {
        try {
            final int methodTagId;
            if (running && (methodTagId = methodTagMaintainer.addMethodTag(method)) >= 0) {
                final Recorder recorder = getRecorder(methodTagId);
                recorder.recordTime(startNanos, System.nanoTime());
            }
        } catch (Throwable t) {
            Logger.error("ProfilingAspect.profiling(" + startNanos + ", " + method + ")", t);
        }
    }

    private static Recorder getRecorder(int methodTagId) {
        final Recorder recorder = recorderMaintainer.getRecorder(methodTagId);
        if (recorder != null) {
            return recorder;
        }

        synchronized (ProfilingAspect.class) {
            final Recorder newRec = recorderMaintainer.getRecorder(methodTagId);
            if (newRec != null) {
                return newRec;
            }

            recorderMaintainer.addRecorder(methodTagId, ProfilingParams.of(3000, 10));
            return recorderMaintainer.getRecorder(methodTagId);
        }
    }

    public static void setRecorderMaintainer(ASMRecorderMaintainer maintainer) {
        synchronized (ProfilingAspect.class) { //强制把值刷新到主存
            recorderMaintainer = maintainer;
        }
    }

    public static void setRunning(boolean run) {
        synchronized (ProfilingAspect.class) { //强制把值刷新到主存
            running = run;
        }
    }
}
