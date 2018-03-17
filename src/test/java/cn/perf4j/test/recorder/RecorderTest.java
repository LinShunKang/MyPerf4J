package cn.perf4j.test.recorder;

import cn.perf4j.*;
import cn.perf4j.util.StopWatch;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class RecorderTest {

    public static void main(String[] args) throws InterruptedException {
        AbstractRecorder recorder = Recorder.getInstance("RecorderTest", 100, 10);

        int times = 100000000;
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < times; ++i) {
            recorder.recordTime(System.nanoTime(), System.nanoTime() + ThreadLocalRandom.current().nextLong(10000000));
        }
        System.out.println(stopWatch.lap("recorder", "round one"));

        System.out.println(recorder.getSortedTimingRecords());
        System.out.println(PerfStatsCalculator.calPerfStat(recorder));
    }
}
