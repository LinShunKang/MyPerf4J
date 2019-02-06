package MyPerf4J.dynamic;

import cn.myperf4j.asm.aop.ProfilingAspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by LinShunkang on 2019/02/06
 */
public class DaoInvocationHandlerV2 implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        long start = System.nanoTime();
        Object o = new Object();
        ProfilingAspect.profiling(start, method);
        return o;
    }
}
