package cn.perf4j;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/15
 */
public class CycleTimingRecorder extends AbstractTimingRecorder {

    private static final long millTimeSlice = 60 * 1000L;//60s

    private static final long nanoTimeSlice = millTimeSlice * 1000 * 1000L;

    private AbstractTimingRecorder nextRecorder;

    private AbstractTimingRecorder curRecorder;

    private volatile long nextNanoTimeSlice = 0L;

    private RecorderProcessor processor;

    private CycleTimingRecorder() {
        //empty
    }

    /**
     * 以下这段代码逻辑借鉴了perf4j；
     * 采用轮转的方式进行，每次只用curRecorder来记录接口的相应时间，如果当前时间大于nextNanoTimeSlice时，把nextRecorder和curRecorder进行轮转；
     * @param startNanoTime
     * @param endNanoTime
     */
    @Override
    public void recordTime(long startNanoTime, long endNanoTime) {
        if (nextNanoTimeSlice == 0L) {
            nextNanoTimeSlice = ((startNanoTime / nanoTimeSlice) * nanoTimeSlice) + nanoTimeSlice;
        }

        //这次记录还在当前的时间片里
        if (nextNanoTimeSlice > startNanoTime) {
            curRecorder.recordTime(startNanoTime, endNanoTime);
            return;
        }

        synchronized (this) {
            if (nextNanoTimeSlice <= startNanoTime) {
                long currentMills = System.currentTimeMillis();
                if (curRecorder.getStartMilliTime() > 0L) {
                    System.err.println("cost: " + (currentMills - curRecorder.getStartMilliTime()) + "ms");
                }

                curRecorder.setStartMilliTime(currentMills - millTimeSlice);
                curRecorder.setStopMilliTime(currentMills);
                nextRecorder.setStartMilliTime(currentMills);

                //调换curRecorder和nextRecorder
                AbstractTimingRecorder tmpRecord = curRecorder;
                curRecorder = nextRecorder;
                nextRecorder = tmpRecord;

                nextNanoTimeSlice = ((startNanoTime / nanoTimeSlice) * nanoTimeSlice) + nanoTimeSlice;
                processor.process(tmpRecord);
            }
        }

        curRecorder.recordTime(startNanoTime, endNanoTime);
    }

    @Override
    public List<TimingRecord> getSortedTimingRecords() {
        return curRecorder.getSortedTimingRecords();
    }

    @Override
    public void resetRecord() {
        curRecorder.resetRecord();
    }

    public static CycleTimingRecorder getInstance(String api, int mostTimeThreshold, int outThresholdCount, RecorderProcessor processor) {
        CycleTimingRecorder result = new CycleTimingRecorder();
        result.setApi(api);
        result.curRecorder = TimingRecorderV2.getRecorder(mostTimeThreshold, outThresholdCount);
        result.curRecorder.setMilliTimeSlice(millTimeSlice);
        result.curRecorder.setApi(api);

        result.nextRecorder = TimingRecorderV2.getRecorder(mostTimeThreshold, outThresholdCount);
        result.nextRecorder.setMilliTimeSlice(millTimeSlice);
        result.nextRecorder.setApi(api);
        result.processor = processor;
        return result;
    }
}
