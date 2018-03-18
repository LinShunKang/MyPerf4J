package cn.perf4j;

import java.util.List;


/**
 * Created by LinShunkang on 2018/3/11
 */
public final class PerfStatsCalculator {

    public static PerfStats calPerfStats(AbstractRecorder recorder) {
        List<Record> sortedRecords = recorder.getSortedTimingRecords();
        return calPerfStats(recorder.getApi(), recorder.getStartMilliTime(), recorder.getStopMilliTime(), sortedRecords);
    }

    public static PerfStats calPerfStats(String api, long startMilliTime, long stopMillTime, List<Record> sortedRecords) {
        int totalCount = getTotalCount(sortedRecords);
        PerfStats result = PerfStats.getInstance(api);
        result.setTotalCount(totalCount);
        result.setStartMillTime(startMilliTime);
        result.setStopMillTime(stopMillTime);

        if (totalCount <= 0) {
            return result;
        }

        result.setMinTime(sortedRecords.get(0).getTime());
        result.setMaxTime(sortedRecords.get(sortedRecords.size() - 1).getTime());

        int[] topPerIndexArr = getTopPercentileIndexArr(totalCount, PerfStats.getPercentiles());
        int[] topPerArr = result.getTPArr();
        int countMile = 0, perIndex = 0;
        for (int i = 0; i < sortedRecords.size(); ++i) {
            Record record = sortedRecords.get(i);
            countMile += record.getCount();
            int index = topPerIndexArr[perIndex];
            if (countMile >= index) {
                topPerArr[perIndex] = record.getTime();
                perIndex++;
            }
        }
        return reviseStatistic(result);
    }

    private static int getTotalCount(List<Record> sortedRecords) {
        if (sortedRecords == null || sortedRecords.isEmpty()) {
            return 0;
        }

        int totalCount = 0;
        for (int i = 0, size = sortedRecords.size(); i < size; ++i) {
            totalCount += sortedRecords.get(i).getCount();
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

    private static int getIndex(int totalCount, double percentile) {
        return (int) Math.ceil(totalCount * percentile);
    }

    private static int[] getTopPercentileIndexArr(int totalCount, double... percentiles) {
        int[] result = new int[percentiles.length];
        for (int i = 0; i < percentiles.length; ++i) {
            result[i] = getIndex(totalCount, percentiles[i]);
        }
        return result;
    }
}
