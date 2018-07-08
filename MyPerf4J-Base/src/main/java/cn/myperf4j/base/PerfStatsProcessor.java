package cn.myperf4j.base;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/15
 */

/**
 * 该接口用于处理MyPerf4J收集到的性能统计信息，MyPerf4J默认提供了 DefaultPerfStatsProcessor
 * 如果需要特殊处理，可以自行实现PerfStatsProcessor接口，并自定义对perfStatsList的处理
 */
public interface PerfStatsProcessor {

    /**
     * @param perfStatsList     : 方法执行统计信息列表，只包含在startMillis和stopMillis时间内有统计数据的方法
     * @param injectMethodCount : 被注入的方法数量
     * @param startMillis       : 此次统计信息的起始时间，单位为ms
     * @param stopMillis        : 此次统计信息的终止时间，单位为ms
     */
    void process(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis);

}
