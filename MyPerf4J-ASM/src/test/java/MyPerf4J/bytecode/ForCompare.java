package MyPerf4J.bytecode;

import cn.myperf4j.asm.aop.ProfilingAspect;

/**
 * Created by LinShunkang on 2023/05/21
 */
public class ForCompare {

    public long getId0(long id) {
        return id + 8;
    }

    public long getId2(long id) {
        long start = System.nanoTime();
        try {
            return id + 8;
        } finally {
            ProfilingAspect.profiling(start, 7);
        }
    }
}
