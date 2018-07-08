package cn.myperf4j.base;

import java.util.List;

/**
 * Created by LinShunkang on 2018/4/9
 */
public class DefaultPerfStatsProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        //You can do anything you want to do :)
        System.out.println(DefaultPerfStatsFormatter.getFormatStr(perfStatsList, injectMethodCount, startMillis, stopMillis));
    }

}
