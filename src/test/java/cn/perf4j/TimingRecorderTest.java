package cn.perf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class TimingRecorderTest {

    public static void main(String[] args) {
        TimingRecorder recorder = TimingRecorder.getRecorder(100, 10);
        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < 900000; ++i) {
            recorder.recordTime(ThreadLocalRandom.current().nextInt(50));
        }
        System.out.println(stopWatch.lap("50", "round one"));

        for (int i = 0; i < 900000; ++i) {
            recorder.recordTime(ThreadLocalRandom.current().nextInt(50, 100));
        }
        System.out.println(stopWatch.lap("100", "round two"));

        for (int i = 0; i < 900000; ++i) {
            recorder.recordTime(ThreadLocalRandom.current().nextInt(200));
        }
        System.out.println(stopWatch.lap("200", "round three"));


//        System.out.println(recorder.getTimingRecords());
        System.out.println(PerfCalculator.calPerfStat(recorder));
        System.out.println(PerfCalculator.calPerfStatV2(recorder));
    }
}
