package cn.perf4j;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/15
 */
public interface RecorderProcessor {

    void process(String api, long startMilliTime, long stopMillTime, List<TimingRecord> sortedRecords);

}
