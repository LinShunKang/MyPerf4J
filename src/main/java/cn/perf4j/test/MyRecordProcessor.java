package cn.perf4j.test;

import cn.perf4j.PerfStatsCalculator;
import cn.perf4j.Record;
import cn.perf4j.RecordProcessor;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class MyRecordProcessor implements RecordProcessor, InitializingBean{

    @Override
    public void process(String api, long startMilliTime, long stopMillTime, List<Record> sortedRecords) {
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
            System.out.println(PerfStatsCalculator.calPerfStat(api, startMilliTime, stopMillTime, sortedRecords));
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
