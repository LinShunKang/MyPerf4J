package cn.perf4j.test;

import cn.perf4j.*;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class TimingRecorderTest {

    public static void main(String[] args) throws InterruptedException {
        AbstractTimingRecorder recorderV2 = TimingRecorderV2.getRecorder(100, 10);

        int times = 100000000;

        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < times; ++i) {
            recorderV2.recordTime(System.nanoTime(), System.nanoTime() + ThreadLocalRandom.current().nextLong(10000000));
        }
        System.out.println(stopWatch.lap("v2", "round two"));


        System.out.println(recorderV2.getSortedTimingRecords());
        System.out.println(PerfCalculator.calPerfStat(recorderV2));
    }
}
