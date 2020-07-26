package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.TypeDescUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/5/20
 */
public final class MethodTagMaintainer extends AbstractMethodTagMaintainer {

    public static final int MAX_NUM = 1024 * 32;

    private static final MethodTagMaintainer instance = new MethodTagMaintainer();

    private static final MetricsConfig metricsConfig = ProfilingConfig.metricsConfig();

    private final AtomicInteger index = new AtomicInteger(0);

    private final AtomicReferenceArray<MethodTag> methodTagArr = new AtomicReferenceArray<>(MAX_NUM);

    private final ConcurrentHashMap<Method, Integer> methodMap = new ConcurrentHashMap<>(4096);

    private MethodTagMaintainer() {
        //empty
    }

    public static MethodTagMaintainer getInstance() {
        return instance;
    }

    @Override
    public int addMethodTag(MethodTag methodTag) {
        int methodId = index.getAndIncrement();
        if (methodId > MAX_NUM) {
            Logger.warn("MethodTagMaintainer.addMethodTag(" + methodTag + "): methodId > MAX_NUM: "
                    + methodId + " > " + MAX_NUM + ", ignored!!!");
            return -1;
        }

        methodTagArr.set(methodId, methodTag);
        return methodId;
    }

    @Override
    public int addMethodTag(Method method) {
        Integer tagId = methodMap.get(method);
        if (tagId != null) {
            return tagId;
        }

        synchronized (this) {
            tagId = methodMap.get(method);
            if (tagId != null) {
                return tagId;
            }

            tagId = addMethodTag(createMethodTag(method));
        }

        if (tagId < 0) {
            return tagId;
        }

        methodMap.putIfAbsent(method, tagId);
        return tagId;
    }

    private static MethodTag createMethodTag(Method method) {
        String methodParamDesc = metricsConfig.showMethodParams() ? TypeDescUtils.getMethodParamsDesc(method) : "";
        Class<?> declaringClass = method.getDeclaringClass();
        return MethodTag.getDynamicProxyInstance(declaringClass.getName(),
                declaringClass.getSimpleName(),
                method.getName(),
                methodParamDesc);
    }

    @Override
    public MethodTag getMethodTag(int methodId) {
        if (methodId >= 0 && methodId < MAX_NUM) {
            return methodTagArr.get(methodId);
        }
        return null;
    }

    @Override
    public int getMethodTagCount() {
        return index.get();
    }
}
