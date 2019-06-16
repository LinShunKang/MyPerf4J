package cn.myperf4j.core.recorder;

import cn.myperf4j.base.buffer.IntBuf;

/**
 * Created by LinShunkang on 2018/3/13
 */
public abstract class Recorder {

    private final int methodTagId;

    public Recorder(int methodTagId) {
        this.methodTagId = methodTagId;
    }

    public int getMethodTagId() {
        return methodTagId;
    }

    public abstract boolean hasRecord();

    public abstract void recordTime(long startNanoTime, long endNanoTime);

    /**
     * 为了节省内存的使用，利用int[]作为返回结果
     *
     * @param intBuf : intBuf.capacity为effectiveRecordCount的两倍!!! 其中，第0位存timeCost，第1位存count，第2位存timeCost，第3位存count，以此类推
     */
    public abstract void fillSortedRecords(IntBuf intBuf);

    /**
     * 获取有效的记录的个数
     */
    public abstract int getEffectiveCount();

    public abstract void resetRecord();

}
