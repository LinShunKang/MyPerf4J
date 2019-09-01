package cn.myperf4j.bench.profiling;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.asm.aop.ProfilingAspect;
import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.bench.EnvUtils;
import org.openjdk.jmh.annotations.*;

/**
 * Created by LinShunkang on 2019/08/31
 */
@State(Scope.Thread)
public class AbstractBench {

    @Setup
    public void setup() {
        boolean initASMBootstrap = EnvUtils.initASMBootstrap(60 * 1000);
        System.out.println("initASMBootstrap=" + initASMBootstrap);

        ASMRecorderMaintainer recorderMaintainer = ASMRecorderMaintainer.getInstance();
        recorderMaintainer.addRecorder(0, ProfilingParams.of(1000, 16));

        ProfilingAspect.setRunning(true);
        ProfilingAspect.setRecorderMaintainer(recorderMaintainer);
    }

    @Benchmark
    public int baseline() {
        return emptyMethod();
    }

    @Benchmark
    public int profiling() {
        long start = System.nanoTime();
        int result = emptyMethod();
        ProfilingAspect.profiling(start, 0);
        return result;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public int emptyMethod() {
        return 1000;
    }

}
