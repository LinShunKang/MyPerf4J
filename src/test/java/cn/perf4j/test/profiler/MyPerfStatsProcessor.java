package cn.perf4j.test.profiler;

import cn.perf4j.PerfStats;
import cn.perf4j.PerfStatsProcessor;
import cn.perf4j.util.DateUtils;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class MyPerfStatsProcessor implements PerfStatsProcessor {

    @Override
    public void process(List<PerfStats> perfStatsList, long startMillis, long stopMillis) {
        //You can do anything you want to do :)
        StringBuilder sb = new StringBuilder((perfStatsList.size() + 1) * 128);
        sb.append("MyPerf4J Performance Statistics [").append(DateUtils.getStr(startMillis)).append(", ").append(DateUtils.getStr(stopMillis)).append("]").append("\n");
        if (perfStatsList.isEmpty()) {
            System.out.println(sb.toString());
            return;
        }

        for (int i = 0; i < perfStatsList.size(); ++i) {
            sb.append(perfStatsList.get(i).toString()).append("\n");
        }
        System.out.println(sb);
    }
}
