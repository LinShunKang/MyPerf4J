package cn.perf4j;

import java.util.Date;
import java.util.List;

/**
 * Created by LinShunkang on 2018/3/15
 */
public class RoundRobinRecorder extends AbstractRecorder {

    private static final long millTimeSlice = 60 * 1000L;//60s
//    private static final long millTimeSlice = 10 * 1000L;//10s

    private static final long nanoTimeSlice = millTimeSlice * 1000 * 1000L;

    private AbstractRecorder nextRecorder;

    private AbstractRecorder curRecorder;

    private volatile long nextNanoTimeSlice = 0L;

    private RecordProcessor processor;

    private RoundRobinRecorder() {
        //empty
    }

    /**
     * 以下这段代码逻辑借鉴了perf4j；
     * 采用轮转的方式进行，每次只用curRecorder来记录接口的相应时间，如果当前时间大于nextNanoTimeSlice时，把nextRecorder和curRecorder进行轮转；
     *
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
                    System.err.println("Time=" + new Date(currentMills) + ", cost: " + (currentMills - curRecorder.getStartMilliTime()) + "ms");
                }

                nextRecorder.resetRecord();

                curRecorder.setStartMilliTime(currentMills - millTimeSlice);
                curRecorder.setStopMilliTime(currentMills);

                //调换curRecorder和nextRecorder
                AbstractRecorder tmp = curRecorder;
                curRecorder = nextRecorder;
                nextRecorder = tmp;

                curRecorder.setStartMilliTime(currentMills);
                curRecorder.setStopMilliTime(currentMills + millTimeSlice);

                nextNanoTimeSlice = ((startNanoTime / nanoTimeSlice) * nanoTimeSlice) + nanoTimeSlice;

                if (!isShutdown()) {
                    processor.process(tmp.getApi(), tmp.getStartMilliTime(), tmp.getStopMilliTime(), tmp.getSortedTimingRecords());
                }
            }
        }

        curRecorder.recordTime(startNanoTime, endNanoTime);
    }

    @Override
    public List<Record> getSortedTimingRecords() {
        return curRecorder.getSortedTimingRecords();
    }

    @Override
    public synchronized void resetRecord() {
        curRecorder.resetRecord();
    }

    public static RoundRobinRecorder getInstance(String api, int mostTimeThreshold, int outThresholdCount, RecordProcessor processor) {
        RoundRobinRecorder result = new RoundRobinRecorder();
        result.setApi(api);
        result.curRecorder = Recorder.getInstance(mostTimeThreshold, outThresholdCount);
        result.curRecorder.setApi(api);

        result.nextRecorder = Recorder.getInstance(mostTimeThreshold, outThresholdCount);
        result.nextRecorder.setApi(api);
        result.processor = processor;
        return result;
    }
}
