package cn.myperf4j.core.scheduler;

/**
 * Created by LinShunkang on 2018/8/22
 */
public interface Scheduler {

    /**
     *
     * @param currentMills: 当前时间戳
     * @param millTimeSlice: 时间片大小
     * @param nextTimeSliceEndTime: 下一个时间片的结束时间
     */
    void run(long currentMills, long millTimeSlice, long nextTimeSliceEndTime);

}
