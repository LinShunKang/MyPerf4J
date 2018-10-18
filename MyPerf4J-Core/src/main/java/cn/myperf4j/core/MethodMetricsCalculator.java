package cn.myperf4j.core;


import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.util.ChunkPool;
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

    public static MethodMetrics calPerfStats(Recorder recorder, MethodTag methodTag, long startTime, long stopTime) {
        int[] sortedRecords = null;
        try {
            int effectiveCount = recorder.getEffectiveCount();
            sortedRecords = ChunkPool.getInstance().getChunk(effectiveCount * 2);
            recorder.fillSortedRecords(sortedRecords);
            return calPerfStats(methodTag, startTime, stopTime, sortedRecords, effectiveCount);
        } catch (Exception e) {
            Logger.error("MethodMetricsCalculator.calPerfStats(" + recorder + ", " + methodTag + ", " + startTime + ", " + stopTime + ")", e);
        } finally {
            ChunkPool.getInstance().returnChunk(sortedRecords);
        }
        return MethodMetrics.getInstance(methodTag, startTime, stopTime);
    }

    private static MethodMetrics calPerfStats(MethodTag methodTag, long startTime, long stopTime, int[] sortedRecords, int effectiveCount) {
        long[] pair = getTotalTimeAndTotalCount(sortedRecords);
        long totalTime = pair[0];
        int totalCount = (int) pair[1];
        MethodMetrics result = MethodMetrics.getInstance(methodTag);
        result.setTotalCount(totalCount);
        result.setStartMillTime(startTime);
        result.setStopMillTime(stopTime);

        if (totalCount <= 0 || effectiveCount <= 0) {
            return result;
        }

        double avgTime = ((double) totalTime) / totalCount;
        result.setAvgTime(avgTime);
        result.setMinTime(sortedRecords[0]);
        result.setMaxTime(sortedRecords[(effectiveCount - 1) * 2]);

        int[] topPerIndexArr = getTopPercentileIndexArr(totalCount);
        int[] topPerArr = result.getTpArr();
        int countMile = 0, perIndex = 0;
        double sigma = 0.0D;//∑
        for (int i = 0, length = sortedRecords.length; i < length; i = i + 2) {
            int timeCost = sortedRecords[i];
            int count = sortedRecords[i + 1];

            //sortedRecords中只有第0位的响应时间可以为0
            if (i > 0 && timeCost <= 0) {
                break;
            }

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

    /**
     * @param sortedRecords
     * @return : long[]: int[0]代表totalTimeCost, int[1]代表totalCount
     */
    private static long[] getTotalTimeAndTotalCount(int[] sortedRecords) {
        long[] result = {0L, 0L};
        if (sortedRecords == null || sortedRecords.length == 0) {
            return result;
        }

        for (int i = 0, length = sortedRecords.length; i < length; i = i + 2) {
            int timeCost = sortedRecords[i];
            int count = sortedRecords[i + 1];

            //sortedRecords中只有第0位的响应时间可以为0
            if (i > 0 && timeCost <= 0) {
                break;
            }

            result[0] += timeCost * count;
            result[1] += count;
        }
        return result;
    }

    private static MethodMetrics reviseStatistic(MethodMetrics methodMetrics) {
        int[] tpArr = methodMetrics.getTpArr();
        for (int i = 1; i < tpArr.length; ++i) {
            int last = tpArr[i - 1];
            int cur = tpArr[i];
            if (cur <= -1) {
                tpArr[i] = last;
            }
        }
        return methodMetrics;
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
