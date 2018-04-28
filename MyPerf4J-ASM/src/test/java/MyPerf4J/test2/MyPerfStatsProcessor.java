package MyPerf4J.test2;

import cn.myperf4j.core.PerfStats;
import cn.myperf4j.core.PerfStatsProcessor;
import cn.myperf4j.core.util.PerfStatsFormatter;

import java.util.List;

/**
 * Created by LinShunkang on 2018/4/26
 */
public class MyPerfStatsProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, long startMillis, long stopMillis) {
        //You can do anything you want to do :)
        System.out.println(PerfStatsFormatter.getFormatStr(perfStatsList, startMillis, stopMillis));
    }
}
