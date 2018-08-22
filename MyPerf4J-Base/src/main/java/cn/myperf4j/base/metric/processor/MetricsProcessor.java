package cn.myperf4j.base.metric.processor;

import cn.myperf4j.base.metric.Metrics;

/**
 * Created by LinShunkang on 2018/8/19
 * <p>
 * 整体处理流程如下：
 * beforeProcess() -> process() -> process() -> *** -> process() -> afterProcess()
 * 即：每一轮统计均以beforeProcess()开始，以afterProcess()结束，中间会多次调用process()
 */
public interface MetricsProcessor<T extends Metrics> {


    /**
     * 在每一轮统计指标开始处理前调用
     *
     * @param processId : 本轮统计的唯一ID
     */
    void beforeProcess(long processId, long startMillis, long stopMillis);


    /**
     * @param metrics     : 本轮统计的某一个指标
     * @param processId   : 本轮统计的唯一ID
     * @param startMillis : 本轮统计信息的起始时间，单位为ms
     * @param stopMillis  : 本轮统计信息的终止时间，单位为ms
     */
    void process(T metrics, long processId, long startMillis, long stopMillis);


    /**
     * 在每一轮统计指标开始处理前调用
     *
     * @param processId : 本轮统计的唯一ID
     */
    void afterProcess(long processId, long startMillis, long stopMillis);

}
