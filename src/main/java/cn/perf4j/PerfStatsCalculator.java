package cn.perf4j;

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
        int[] sortedRecords = recorder.getSortedTimingRecords();
        return calPerfStats(recorder.getApi(), recorder.getStartMilliTime(), recorder.getStopMilliTime(), sortedRecords);
    }

    public static PerfStats calPerfStats(String api, long startMilliTime, long stopMillTime, int[] sortedRecords) {
        int totalCount = getTotalCount(sortedRecords);
        PerfStats result = PerfStats.getInstance(api);
        result.setTotalCount(totalCount);
        result.setStartMillTime(startMilliTime);
        result.setStopMillTime(stopMillTime);

        if (totalCount <= 0) {
            return result;
        }

        result.setMinTime(sortedRecords[1]);
        result.setMaxTime(sortedRecords[sortedRecords.length - 1]);

        int[] topPerIndexArr = getTopPercentileIndexArr(totalCount);
        int[] topPerArr = result.getTPArr();
        int countMile = 0, perIndex = 0;
        for (int i = 0, length = sortedRecords.length; i < length; i = i + 2) {
            countMile += sortedRecords[i + 1];
            int index = topPerIndexArr[perIndex];
            if (countMile >= index) {
                topPerArr[perIndex] = sortedRecords[i];
                perIndex++;
            }
        }
        return reviseStatistic(result);
    }

    private static int getTotalCount(int[] sortedRecords) {
        if (sortedRecords == null || sortedRecords.length == 0) {
            return 0;
        }

        int totalCount = 0;
        for (int i = 0, length = sortedRecords.length; i < length; i = i + 2) {
            totalCount += sortedRecords[i + 1];
        }
        return totalCount;
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
