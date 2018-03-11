package cn.perf4j;

import cn.perf4j.utils.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class TimingRecorder {

    private final int mostTimeThreshold;

    private final AtomicInteger[] timingArr;

    private final ConcurrentHashMap<Integer, AtomicInteger> timingMap;

    private TimingRecorder(int mostTimeThreshold, int outThresholdCount) {
        this.mostTimeThreshold = mostTimeThreshold;
        this.timingArr = new AtomicInteger[mostTimeThreshold];
        for (int i = 0; i < mostTimeThreshold; ++i) {
            timingArr[i] = new AtomicInteger(0);
        }

        this.timingMap = new ConcurrentHashMap<>(MapUtils.getFitCapacity(outThresholdCount));
    }

    public void recordTime(int elapsedTime) {
        if (elapsedTime < mostTimeThreshold) {
            timingArr[elapsedTime].incrementAndGet();
            return;
        }

        if (!timingMap.containsKey(elapsedTime)) {
            timingMap.putIfAbsent(elapsedTime, new AtomicInteger(0));
        }
        timingMap.get(elapsedTime).incrementAndGet();
    }

    /**
     * @return sorted TimingRecord list
     */
    public List<TimingRecord> getTimingRecords() {
        List<TimingRecord> result = new ArrayList<>(timingArr.length + timingMap.size());
        for (int i = 0; i < timingArr.length; ++i) {
            if (timingArr[i].get() > 0) {
                result.add(TimingRecord.getInstance(i, timingArr[i].get()));
            }
        }
        result.addAll(getSortedMapRecords());
        return result;
    }

    private List<TimingRecord> getSortedMapRecords() {
        List<TimingRecord> mapRecords = new ArrayList<>(timingMap.size());
        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            mapRecords.add(TimingRecord.getInstance(entry.getKey(), entry.getValue().get()));
        }
        Collections.sort(mapRecords, new Comparator<TimingRecord>() {
            @Override
            public int compare(TimingRecord o1, TimingRecord o2) {
                return o1.getTime() - o2.getTime();
            }
        });
        return mapRecords;
    }

    public static TimingRecorder getRecorder(int mostTimeThreshold, int outThresholdCount) {
        return new TimingRecorder(mostTimeThreshold, outThresholdCount);
    }
}
