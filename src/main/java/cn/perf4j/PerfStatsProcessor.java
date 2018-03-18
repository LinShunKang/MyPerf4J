package cn.perf4j;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/15
 */
public interface PerfStatsProcessor {

    void process(List<PerfStats> perfStatsList, long startMillis, long stopMillis);

}
