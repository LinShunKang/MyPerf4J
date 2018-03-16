package cn.perf4j;

import cn.perf4j.utils.ThreadUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/15
 */
public class RoundRobinTimingRecorder extends AbstractTimingRecorder {

    private static final long millTimeSlice = 60 * 1000L;//60s

    private static final long nanoTimeSlice = millTimeSlice * 1000 * 1000L;

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.HOURS, new LinkedBlockingQueue<Runnable>(1000), ThreadUtils.newThreadFactory("MyPerf4J-CycleTimingRecorder_"), new ThreadPoolExecutor.DiscardPolicy());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("RoundRobinTimingRecorder.shutdown hook...");
                    executor.shutdown();
                    executor.awaitTermination(10, TimeUnit.SECONDS);
                } catch (Exception e) {
                    System.err.println("");
                }
            }
        }));
    }

    private AbstractTimingRecorder nextRecorder;

    private AbstractTimingRecorder curRecorder;

    private volatile long nextNanoTimeSlice = 0L;

    private RecorderProcessor processor;

    private RoundRobinTimingRecorder() {
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
                final AbstractTimingRecorder tmp = curRecorder;
                curRecorder = nextRecorder;
                nextRecorder = tmp;

                curRecorder.setStartMilliTime(currentMills);
                curRecorder.setStopMilliTime(currentMills + millTimeSlice);

                nextNanoTimeSlice = ((startNanoTime / nanoTimeSlice) * nanoTimeSlice) + nanoTimeSlice;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        processor.process(tmp.getApi(), tmp.getStartMilliTime(), tmp.getStopMilliTime(), tmp.getSortedTimingRecords());
                    }
                });
            }
        }

        curRecorder.recordTime(startNanoTime, endNanoTime);
    }

    @Override
    public List<TimingRecord> getSortedTimingRecords() {
        return curRecorder.getSortedTimingRecords();
    }

    @Override
    public synchronized void resetRecord() {
        curRecorder.resetRecord();
    }

    public static RoundRobinTimingRecorder getInstance(String api, int mostTimeThreshold, int outThresholdCount, RecorderProcessor processor) {
        RoundRobinTimingRecorder result = new RoundRobinTimingRecorder();
        result.setApi(api);
        result.curRecorder = TimingRecorder.getInstance(mostTimeThreshold, outThresholdCount);
        result.curRecorder.setApi(api);

        result.nextRecorder = TimingRecorder.getInstance(mostTimeThreshold, outThresholdCount);
        result.nextRecorder.setApi(api);
        result.processor = processor;
        return result;
    }
}
