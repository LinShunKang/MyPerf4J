package cn.myperf4j.bench.profiling;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.asm.aop.ProfilingAspect;
import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.base.util.concurrent.ExecutorManager;
import cn.myperf4j.bench.EnvUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openjdk.jmh.annotations.CompilerControl.Mode.DONT_INLINE;

/**
 * Created by LinShunkang on 2019/08/31
 */
@State(Scope.Thread)
public class AbstractBench {

    @Setup
    public void setup() {
        System.out.println("tearDown() starting...");

        final boolean initASMBootstrap = EnvUtils.initASMBootstrap(60 * 1000);
        System.out.println("initASMBootstrap=" + initASMBootstrap);

        final ASMRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();
        maintainer.addRecorder(0, ProfilingParams.of(1000, 16));

        ProfilingAspect.setRunning(true);
        ProfilingAspect.setRecorderMaintainer(maintainer);
    }

    @TearDown
    public void tearDown() {
        System.out.println("tearDown() starting...");
        ExecutorManager.stopAll(1, SECONDS);
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

    @CompilerControl(DONT_INLINE)
    public int emptyMethod() {
        return 1000;
    }
}
