package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.core.AbstractRecorder;
import cn.myperf4j.core.util.Logger;

/**
 * Created by LinShunkang on 2018/4/15
 */
public final class ProfilingAspect {

    private static ASMRecorderMaintainer recorderMaintainer;

    private static boolean running = false;

    public static void profiling(long startNanos, String tag) {
        try {
            if (!running) {
                Logger.warn("ProfilingAspect.doProfiling(): tag=" + tag + ", startNanos: " + startNanos + ", IGNORED!!!");
                return;
            }

            AbstractRecorder recorder = recorderMaintainer.getRecorder(tag);
            if (recorder == null) {
                Logger.warn("ProfilingAspect.doProfiling(): tag=" + tag + ", startNanos: " + startNanos + ", recorder is null IGNORED!!!");
                return;
            }

            long stopNanos = System.nanoTime();
            recorder.recordTime(startNanos, stopNanos);
        } catch (Exception e) {
            Logger.error("ProfilingAspect.doProfiling(" + startNanos + ", " + tag + ")", e);
        }
    }

    public static void setRecorderMaintainer(ASMRecorderMaintainer maintainer) {
        recorderMaintainer = maintainer;
    }

    public static void setRunning(boolean run) {
        running = run;
    }
}
