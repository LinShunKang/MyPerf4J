package cn.myperf4j.base.psp;

import cn.myperf4j.base.PerfStats;
import cn.myperf4j.base.PerfStatsProcessor;

import java.util.List;

/**
 * Created by LinShunkang on 2018/4/9
 */
public class DefaultStdoutProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        System.out.println(DefaultFormatter.getFormatStr(perfStatsList, injectMethodCount, startMillis, stopMillis));
    }

}
