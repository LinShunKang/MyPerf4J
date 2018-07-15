package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;

/**
 * Created by LinShunkang on 2018/5/20
 */
public abstract class AbstractMethodTagMaintainer {

    public abstract int addMethodTag(MethodTag methodTag);

    public abstract MethodTag getMethodTag(int methodId);

    public abstract int getMethodTagCount();

}
