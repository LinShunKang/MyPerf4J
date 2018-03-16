package cn.perf4j.test;

import cn.perf4j.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class TimingRecorderTest {

    public static void main(String[] args) throws InterruptedException {
        AbstractTimingRecorder recorder = TimingRecorder.getInstance(100, 10);
        AbstractTimingRecorder recorderV2 = RoundRobinTimingRecorder.getInstance("", 100, 10, new RecorderProcessor() {
            @Override
            public void process(String api, long startMilliTime, long stopMillTime, List<TimingRecord> sortedRecords) {
                //empty
            }
        });

        int times = 100000000;
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < times; ++i) {
            recorder.recordTime(System.nanoTime(), System.nanoTime() + ThreadLocalRandom.current().nextLong(10000000));
        }
        System.out.println(stopWatch.lap("recorder", "round one"));

        for (int i = 0; i < times; ++i) {
            recorderV2.recordTime(System.nanoTime(), System.nanoTime() + ThreadLocalRandom.current().nextLong(10000000));
        }
        System.out.println(stopWatch.lap("recorderV2", "round one"));

        System.out.println(recorderV2.getSortedTimingRecords());
        System.out.println(PerfStatsCalculator.calPerfStat(recorderV2));
    }
}
