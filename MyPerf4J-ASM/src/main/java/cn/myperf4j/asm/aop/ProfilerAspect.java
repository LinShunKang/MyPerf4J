package cn.myperf4j.asm.aop;

import cn.myperf4j.core.AbstractRecorder;
import cn.myperf4j.core.RecorderMaintainer;
import cn.myperf4j.core.util.Logger;

/**
 * Created by LinShunkang on 2018/4/15
 */
public final class ProfilerAspect {

    private static RecorderMaintainer recorderMaintainer;

    private static boolean running = false;

    static {
        try {
            Class.forName("cn.myperf4j.asm.MyASMBootstrap");
        } catch (Throwable e) {
            Logger.error("ProfilerAspect: loadClass cn.myperf4j.asm.MyASMBootstrap failure!!!", e);
        }
    }

    public static void profiling(long startNanos, String tag) {
        try {
            if (!running) {
                Logger.warn("ProfilerAspect.doProfiling(): tag=" + tag + ", startNanos: " + startNanos + ", IGNORED!!!");
                return;
            }

            AbstractRecorder recorder = recorderMaintainer.getRecorder(tag);
            if (recorder == null) {
                Logger.warn("ProfilerAspect.doProfiling(): tag=" + tag + ", startNanos: " + startNanos + ", recorder is null IGNORED!!!");
                return;
            }

            long stopNanos = System.nanoTime();
            recorder.recordTime(startNanos, stopNanos);
//            Logger.info("ProfilerAspect.doProfiling(): tag=" + tag + ", cost: " + (stopNanos - startNanos) + "ns, RECORDED!!!");
        } catch (Exception e) {
            Logger.error("ProfilerAspect.doProfiling(" + startNanos + ", " + tag + ")", e);
        }
    }

    public static void setRecorderMaintainer(RecorderMaintainer recorderMaintainer) {
        ProfilerAspect.recorderMaintainer = recorderMaintainer;
    }

    public static void setRunning(boolean running) {
        ProfilerAspect.running = running;
    }
}
