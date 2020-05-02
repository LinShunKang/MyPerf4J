package cn.myperf4j.base;

/**
 * Created by LinShunkang on 2018/8/22
 */
public interface Scheduler {

    /**
     * @param lastTimeSliceStartTime: 上一个时间片的起始时间
     * @param millTimeSlice:          时间片大小
     */
    void run(long lastTimeSliceStartTime, long millTimeSlice);

    String name();

}
