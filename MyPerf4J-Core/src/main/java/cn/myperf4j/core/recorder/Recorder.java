package cn.myperf4j.core.recorder;

import cn.myperf4j.base.buffer.LongBuf;

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
     * 为了节省内存的使用，利用 LongBuf 承载返回结果
     *
     * @param longBuf : intBuf#capacity 至少为 diffCount!
     *                其中：
     *                第 0 位存 ((long)timeCost << 32) | count，
     *                第 1 位存 ((long)timeCost << 32) | count，
     *                以此类推
     * @return 总请求数
     */
    public abstract long fillSortedRecords(LongBuf longBuf);

    /**
     * 获取有效的记录的个数
     */
    public abstract int getDiffCount();

    public abstract void resetRecord();
}
