package cn.myperf4j.core.util;


import cn.myperf4j.base.PerfStats;
import cn.myperf4j.core.AbstractRecorder;

/**
 * Created by LinShunkang on 2018/3/11
 */
public final class PerfStatsCalculator {

    //由于调用PerfStatsCalculator.calPerfStats()方法的线程只有一个，
    // 并且PerfStats.getPercentiles().length是一定的，所以可以用ThreadLocal进行复用
    private static final ThreadLocal<int[]> threadLocalIntArr = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[PerfStats.getPercentiles().length];
        }
    };

    public static PerfStats calPerfStats(AbstractRecorder recorder) {
        int[] sortedRecords = null;
        try {
            int effectiveCount = recorder.getEffectiveCount();
            sortedRecords = ChunkPool.getInstance().getChunk(effectiveCount * 2);
            recorder.fillSortedRecords(sortedRecords);
            return calPerfStats(recorder.getTag(), recorder.getStartTime(), recorder.getStopTime(), sortedRecords, effectiveCount);
        } catch (Exception e) {
            Logger.error("PerfStatsCalculator.calPerfStats(" + recorder + ")", e);
        } finally {
            ChunkPool.getInstance().returnChunk(sortedRecords);
        }
        return PerfStats.getInstance(recorder.getTag(), recorder.getStartTime(), recorder.getStopTime());
    }

    private static PerfStats calPerfStats(String api, long startTime, long stopTime, int[] sortedRecords, int effectiveCount) {
        int[] pair = getTotalTimeAndTotalCount(sortedRecords);
        int totalTime = pair[0];
        int totalCount = pair[1];
        PerfStats result = PerfStats.getInstance(api);
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
        int[] topPerArr = result.getTPArr();
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
     * @return : int[]: int[0]代表totalTimeCost, int[1]代表totalCount
     */
    private static int[] getTotalTimeAndTotalCount(int[] sortedRecords) {
        int[] result = {0, 0};
        if (sortedRecords == null || sortedRecords.length == 0) {
            return result;
        }

        for (int i = 0, length = sortedRecords.length; i < length; i = i + 2) {
            int timeCost = sortedRecords[i];

            //sortedRecords中只有第0位的响应时间可以为0
            if (i > 0 && timeCost <= 0) {
                break;
            }

            result[0] += timeCost;
            result[1] += sortedRecords[i + 1];//count
        }
        return result;
    }

    private static PerfStats reviseStatistic(PerfStats perfStats) {
        int[] tpArr = perfStats.getTPArr();
        for (int i = 1; i < tpArr.length; ++i) {
            int last = tpArr[i - 1];
            int cur = tpArr[i];
            if (cur <= -1) {
                tpArr[i] = last;
            }
        }
        return perfStats;
    }

    private static int[] getTopPercentileIndexArr(int totalCount) {
        int[] result = threadLocalIntArr.get();
        double[] percentiles = PerfStats.getPercentiles();
        for (int i = 0; i < percentiles.length; ++i) {
            result[i] = getIndex(totalCount, percentiles[i]);
        }
        return result;
    }

    private static int getIndex(int totalCount, double percentile) {
        return (int) Math.ceil(totalCount * percentile);
    }
}
