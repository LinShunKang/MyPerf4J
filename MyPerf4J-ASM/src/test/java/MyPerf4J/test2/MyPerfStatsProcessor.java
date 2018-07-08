package MyPerf4J.test2;

import cn.myperf4j.base.PerfStats;
import cn.myperf4j.base.DefaultPerfStatsFormatter;
import cn.myperf4j.base.PerfStatsProcessor;

import java.util.List;

/**
 * Created by LinShunkang on 2018/4/26
 */
public class MyPerfStatsProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        //You can do anything you want to do :)
        System.out.println(DefaultPerfStatsFormatter.getFormatStr(perfStatsList, injectMethodCount, startMillis, stopMillis));
    }
}
