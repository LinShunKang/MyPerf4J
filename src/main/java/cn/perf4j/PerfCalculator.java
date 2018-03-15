package cn.perf4j;

import java.util.List;


/**
 * Created by LinShunkang on 2018/3/11
 */

/**
 * Top Percentile calculator
 */
public final class PerfCalculator {

    public static PerfStatistic calPerfStat(AbstractTimingRecorder recorder) {
        List<TimingRecord> sortedRecords = recorder.getSortedTimingRecords();
        if (sortedRecords == null || sortedRecords.isEmpty()) {
            return PerfStatistic.getInstance(recorder.getApi());
        }

        int totalCount = getTotalCount(sortedRecords);
        PerfStatistic result = PerfStatistic.getInstance(recorder.getApi());
        result.setTotalCount(totalCount);
        result.setTimeslice(recorder.getMilliTimeSlice());
        result.setStartMillTime(recorder.getStartMilliTime());
        result.setStopMillTime(recorder.getStopMilliTime());
        result.setMinTime(sortedRecords.get(0).getTime());
        result.setMaxTime(sortedRecords.get(sortedRecords.size() - 1).getTime());

        int[] perIndexArr = getPercentileIndexArr(totalCount, result.getPercentiles());
        int[] records = result.getTpArr();
        int countMile = 0, perIndex = 0;
        for (int i = 0; i < sortedRecords.size(); ++i) {
            TimingRecord record = sortedRecords.get(i);
            countMile += record.getCount();
            int index = perIndexArr[perIndex];
            if (countMile >= index) {
                records[perIndex] = record.getTime();
                perIndex++;
            }
        }
        return reviseStatistic(result);
    }

    private static int getTotalCount(List<TimingRecord> sortedRecords) {
        int totalCount = 0;
        for (int i = 0, size = sortedRecords.size(); i < size; ++i) {
            totalCount += sortedRecords.get(i).getCount();
        }
        return totalCount;
    }

    private static PerfStatistic reviseStatistic(PerfStatistic perfStatistic) {
        int[] tpArr = perfStatistic.getTpArr();
        for (int i = 1; i < tpArr.length; ++i) {
            int last = tpArr[i - 1];
            int cur = tpArr[i];
            if (cur <= -1) {
                tpArr[i] = last;
            }
        }
        return perfStatistic;
    }

    private static int getIndex(int totalCount, double percentile) {
        return (int) Math.ceil(totalCount * percentile);
    }

    private static int[] getPercentileIndexArr(int totalCount, double... percentiles) {
        int[] result = new int[percentiles.length];
        for (int i = 0; i < percentiles.length; ++i) {
            result[i] = getIndex(totalCount, percentiles[i]);
        }
        return result;
    }
}
