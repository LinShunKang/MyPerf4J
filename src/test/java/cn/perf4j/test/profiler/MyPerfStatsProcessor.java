package cn.perf4j.test.profiler;

import cn.perf4j.PerfStats;
import cn.perf4j.PerfStatsProcessor;
import cn.perf4j.util.PerfStatsFormatter;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class MyPerfStatsProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, long startMillis, long stopMillis) {
        //You can do anything you want to do :)
        System.out.println(PerfStatsFormatter.getFormatStr(perfStatsList, startMillis, stopMillis));
    }

}
