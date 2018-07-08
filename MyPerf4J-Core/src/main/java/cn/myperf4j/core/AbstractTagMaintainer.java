package cn.myperf4j.core;

/**
 * Created by LinShunkang on 2018/5/20
 */
public abstract class AbstractTagMaintainer {

    public abstract int addTag(String tag);

    public abstract String getTag(int tagId);

    public abstract int getTagCount();

}
