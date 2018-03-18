package cn.perf4j.test.profiler;

import cn.perf4j.PerfStatsCalculator;
import cn.perf4j.Record;
import cn.perf4j.RecordProcessor;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class MyRecordProcessor implements RecordProcessor {

    @Override
    public void process(String api, long startMilliTime, long stopMillTime, List<Record> sortedRecords) {
        System.out.println(PerfStatsCalculator.calPerfStats(api, startMilliTime, stopMillTime, sortedRecords));
    }
}
