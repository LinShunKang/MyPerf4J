package cn.perf4j;

import cn.perf4j.utils.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2018/3/13
 */
public class Recorder extends AbstractRecorder {

    private final int mostTimeThreshold;

    private final AtomicIntegerArray timingArr;

    private final ConcurrentHashMap<Integer, AtomicInteger> timingMap;

    private Recorder(int mostTimeThreshold, int outThresholdCount) {
        this.mostTimeThreshold = mostTimeThreshold;
        this.timingArr = new AtomicIntegerArray(mostTimeThreshold);
        this.timingMap = new ConcurrentHashMap<>(MapUtils.getFitCapacity(outThresholdCount));
    }

    @Override
    public void recordTime(long startNanoTime, long endNanoTime) {
        int elapsedTime = (int) ((endNanoTime - startNanoTime) / 1000000);
        if (elapsedTime < mostTimeThreshold) {
            timingArr.incrementAndGet(elapsedTime);
            return;
        }

        AtomicInteger count = timingMap.get(elapsedTime);
        if (count != null) {
            count.incrementAndGet();
            return;
        }

        timingMap.putIfAbsent(elapsedTime, new AtomicInteger(0));
        timingMap.get(elapsedTime).incrementAndGet();
    }

    @Override
    public List<Record> getSortedTimingRecords() {
        List<Record> result = new ArrayList<>(timingArr.length() + timingMap.size());
        for (int i = 0; i < timingArr.length(); ++i) {
            int count = timingArr.get(i);
            if (count > 0) {
                result.add(Record.getInstance(i, count));
            }
        }
        result.addAll(getSortedMapRecords());
        return result;
    }

    private List<Record> getSortedMapRecords() {
        List<Record> mapRecords = new ArrayList<>(timingMap.size());
        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                mapRecords.add(Record.getInstance(entry.getKey(), entry.getValue().get()));
            }
        }

        Collections.sort(mapRecords, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getTime() - o2.getTime();
            }
        });
        return mapRecords;
    }

    @Override
    public synchronized void resetRecord() {
        for (int i = 0; i < timingArr.length(); ++i) {
            timingArr.set(i, 0);
        }

        Iterator<Map.Entry<Integer, AtomicInteger>> iterator = timingMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, AtomicInteger> entry = iterator.next();
            if (entry.getKey() > (1.5 * mostTimeThreshold)) {
                iterator.remove();
            } else {
                entry.getValue().set(0);
            }
        }

        setStartMilliTime(0L);
        setStopMilliTime(0L);
    }

    public static Recorder getInstance(int mostTimeThreshold, int outThresholdCount) {
        return new Recorder(mostTimeThreshold, outThresholdCount);
    }
}
