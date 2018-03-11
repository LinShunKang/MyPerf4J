package cn.perf4j;

import java.util.List;


/**
 * Created by LinShunkang on 2018/3/11
 */

/**
 * Top Percentile calculator
 */
public final class PerfCalculator {

    public static PerfStatistic calPerfStat(TimingRecorder recorder) {
        List<TimingRecord> sortedRecords = recorder.getTimingRecords();
        long totalCount = getTotalCount(sortedRecords);

        PerfStatistic result = PerfStatistic.getInstance();
        int countMile = 0;
        for (int i = 0, size = sortedRecords.size(); i < size; ++i) {
            TimingRecord record = sortedRecords.get(i);
            int time = record.getTime();
            int curCount = record.getCount();

            countMile += curCount;
            double rate = ((double) countMile / totalCount);
            if (rate <= 0.50D && curCount > 0) {
                setTimeRecord(result.getTp50(), countMile, time);
            } else if (rate > 0.50D && rate <= 0.90D && curCount > 0) {
                setTimeRecord(result.getTp90(), countMile, time);
            } else if (rate > 0.90D && rate <= 0.95D && curCount > 0) {
                setTimeRecord(result.getTp95(), countMile, time);
            } else if (rate > 0.95D && rate <= 0.99D && curCount > 0) {
                setTimeRecord(result.getTp99(), countMile, time);
            } else if (rate > 0.99D && rate <= 0.999D && curCount > 0) {
                setTimeRecord(result.getTp999(), countMile, time);
            } else if (rate > 0.999D && rate <= 0.9999D && curCount > 0) {
                setTimeRecord(result.getTp9999(), countMile, time);
            } else if (rate > 0.99999D && curCount > 0) {
                setTimeRecord(result.getTp100(), countMile, time);
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

    private static void setTimeRecord(TimingRecord record, int count, int time) {
        record.setTime(time);
        record.setCount(count);
    }

    private static PerfStatistic reviseStatistic(PerfStatistic perfStatistic) {
        TimingRecord[] tpArr = perfStatistic.getTpArr();
        for (int i = 1; i < tpArr.length; ++i) {
            TimingRecord last = tpArr[i - 1];
            TimingRecord cur = tpArr[i];
            if (cur.getCount() <= 0) {
                cur.setCount(last.getCount());
                cur.setTime(last.getTime());
            }
        }
        return perfStatistic;
    }


    public static PerfStatistic calPerfStatV2(TimingRecorder recorder) {
        List<TimingRecord> sortedRecords = recorder.getTimingRecords();
        if (sortedRecords == null || sortedRecords.isEmpty()) {
            return PerfStatistic.getInstance();
        }

        int totalCount = getTotalCount(sortedRecords);
        PerfStatistic result = PerfStatistic.getInstance();
        result.setMinTime(sortedRecords.get(0).getTime());
        result.setMaxTime(sortedRecords.get(sortedRecords.size() - 1).getTime());

        result.setTp50(getTP(sortedRecords, totalCount, 0.5D));
        result.setTp90(getTP(sortedRecords, totalCount, 0.9D));
        result.setTp95(getTP(sortedRecords, totalCount, 0.95D));
        result.setTp99(getTP(sortedRecords, totalCount, 0.99D));
        result.setTp999(getTP(sortedRecords, totalCount, 0.999));
        result.setTp9999(getTP(sortedRecords, totalCount, 0.9999D));
        result.setTp100(getTP(sortedRecords, totalCount, 1.0D));

        return result;
    }

    private static TimingRecord getTP(List<TimingRecord> sortedRecords, int totalCount, double percentile) {
        int index = (int) Math.ceil(totalCount * percentile);
        int countMile = 0;
        for (int i = 0; i < sortedRecords.size(); ++i) {
            TimingRecord record = sortedRecords.get(i);
            countMile += record.getCount();
            if (countMile >= index) {
                return TimingRecord.getInstance(record.getTime(), index);
            }
        }
        return sortedRecords.get(sortedRecords.size() - 1);
    }
}
