package cn.perf4j.test.recorder;

import cn.perf4j.AccurateRecorder;
import cn.perf4j.util.ChunkPool;
import cn.perf4j.util.PerfStatsCalculator;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2018/4/3
 */
public class RecorderTest {
    public static void main(String[] args) {
        int mostTimeThreshold = 50000;
        int outThresholdCount = 50000;
        AccurateRecorder recorder = AccurateRecorder.getInstance("111", mostTimeThreshold, outThresholdCount);
        long startNano = System.nanoTime();
        for (long i = 1; i <= mostTimeThreshold + outThresholdCount; ++i) {
            recorder.recordTime(startNano, startNano + i * 1000 * 1000);
        }

        int length = recorder.getEffectiveCount();
        int[] sortedRecords = ChunkPool.getInstance().getChunk(length * 2);
        recorder.fillSortedRecords(sortedRecords);
        System.out.println(Arrays.toString(sortedRecords));
        System.out.println(PerfStatsCalculator.calPerfStats(recorder));
        ChunkPool.getInstance().returnChunk(sortedRecords);

    }
}
