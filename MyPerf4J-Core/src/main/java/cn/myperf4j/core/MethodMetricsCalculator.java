package cn.myperf4j.core;

import cn.myperf4j.base.buffer.IntBuf;
import cn.myperf4j.base.buffer.IntBufPool;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.core.recorder.Recorder;

/**
 * Created by LinShunkang on 2018/3/11
 */
public final class MethodMetricsCalculator {

    private static final ThreadLocal<int[]> threadLocalIntArr = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[MethodMetrics.getPercentiles().length];
        }
    };

    private static final IntBufPool intBufPool = IntBufPool.getInstance();

    public static MethodMetrics calPerfStats(Recorder recorder, MethodTag methodTag, long startTime, long stopTime) {
        IntBuf intBuf = null;
        try {
            int effectiveCount = recorder.getEffectiveCount();
            intBuf = intBufPool.acquire(effectiveCount << 1);
            recorder.fillSortedRecords(intBuf);
            return calPerfStats(methodTag, startTime, stopTime, intBuf, effectiveCount);
        } catch (Exception e) {
            Logger.error("MethodMetricsCalculator.calPerfStats(" + recorder + ", " + methodTag + ", " + startTime + ", " + stopTime + ")", e);
        } finally {
            intBufPool.release(intBuf);
        }
        return MethodMetrics.getInstance(methodTag, startTime, stopTime);
    }

    private static MethodMetrics calPerfStats(MethodTag methodTag,
                                              long startTime,
                                              long stopTime,
                                              IntBuf sortedRecords,
                                              int effectiveCount) {
        MethodMetrics result = MethodMetrics.getInstance(methodTag, startTime, stopTime);
        if (effectiveCount <= 0) {
            return result;
        }
        calAvgTime(result, sortedRecords);

        result.setMinTime(sortedRecords.getInt(0));
        result.setMaxTime(sortedRecords.getInt(sortedRecords.writerIndex() - 2));

        int totalCount = result.getTotalCount();
        int[] topPerIndexArr = getTopPercentileIndexArr(totalCount);
        int[] topPerArr = result.getTpArr();
        int countMile = 0, perIndex = 0;
        double sigma = 0.0D;//∑
        double avgTime = result.getAvgTime();
        for (int i = 0; i < sortedRecords.writerIndex(); ) {
            int timeCost = sortedRecords.getInt(i++);
            int count = sortedRecords.getInt(i++);

            countMile += count;
            int index = topPerIndexArr[perIndex];
            if (countMile >= index) {
                topPerArr[perIndex] = timeCost;
                perIndex++;
            }

            sigma += Math.pow(timeCost - avgTime, 2.0);
        }
        result.setStdDev(Math.sqrt(sigma / totalCount));

        return reviseStatistic(result);
    }

    private static void calAvgTime(MethodMetrics metrics, IntBuf sortedRecords) {
        if (sortedRecords == null || sortedRecords.writerIndex() <= 0) {
            return;
        }

        long totalTime = 0L;
        int totalCount = 0;
        for (int i = 0; i < sortedRecords.writerIndex(); ) {
            int timeCost = sortedRecords.getInt(i++);
            int count = sortedRecords.getInt(i++);

            totalTime += timeCost * count;
            totalCount += count;
        }
        metrics.setTotalCount(totalCount);
        metrics.setAvgTime(((double) totalTime) / totalCount);
    }

    private static MethodMetrics reviseStatistic(MethodMetrics metrics) {
        int[] tpArr = metrics.getTpArr();
        for (int i = 1; i < tpArr.length; ++i) {
            int last = tpArr[i - 1];
            int cur = tpArr[i];
            if (cur <= -1) {
                tpArr[i] = last;
            }
        }
        return metrics;
    }

    private static int[] getTopPercentileIndexArr(int totalCount) {
        int[] result = threadLocalIntArr.get();
        double[] percentiles = MethodMetrics.getPercentiles();
        for (int i = 0; i < percentiles.length; ++i) {
            result[i] = getIndex(totalCount, percentiles[i]);
        }
        return result;
    }

    private static int getIndex(int totalCount, double percentile) {
        return (int) Math.ceil(totalCount * percentile);
    }
}
