package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.core.util.Logger;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/5/20
 */
public class MethodTagMaintainer extends AbstractMethodTagMaintainer {

    static final int MAX_NUM = 1024 * 128;

    private final AtomicInteger index = new AtomicInteger(0);

    private final AtomicReferenceArray<MethodTag> methodTagArr = new AtomicReferenceArray<>(MAX_NUM);

    private static final MethodTagMaintainer instance = new MethodTagMaintainer();

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
            Logger.warn("MethodTagMaintainer.addMethodTag(" + methodTag + "): methodId > MAX_NUM: " + methodId + " > " + MAX_NUM + ", ignored!!!");
            return -1;
        }

        methodTagArr.set(methodId, methodTag);
        return methodId;
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
