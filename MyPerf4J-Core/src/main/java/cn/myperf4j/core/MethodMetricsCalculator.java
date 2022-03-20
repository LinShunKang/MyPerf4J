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

    private static final ThreadLocal<long[]> LONG_ARR_TL = new ThreadLocal<long[]>() {
        @Override
        protected long[] initialValue() {
            return new long[MethodMetrics.getPercentiles().length];
        }
    };

    private static final IntBufPool intBufPool = IntBufPool.getInstance();

    private MethodMetricsCalculator() {
        //empty
    }

    public static MethodMetrics calMetrics(Recorder recorder, MethodTag methodTag, long startTime, long stopTime) {
        IntBuf intBuf = null;
        try {
            final int diffCount = recorder.getDiffCount();
            intBuf = intBufPool.acquire(diffCount << 1);
            final long totalCount = recorder.fillSortedRecords(intBuf);
            return calMetrics(recorder, methodTag, startTime, stopTime, intBuf, totalCount, diffCount);
        } catch (Exception e) {
            Logger.error("MethodMetricsCalculator.calMetrics(" + recorder + ", " + methodTag + ", "
                    + startTime + ", " + stopTime + "): infBuf=" + intBuf, e);
        } finally {
            intBufPool.release(intBuf);
        }
        return MethodMetrics.getInstance(methodTag, recorder.getMethodTagId(), startTime, stopTime);
    }

    private static MethodMetrics calMetrics(Recorder recorder,
                                            MethodTag methodTag,
                                            long startTime,
                                            long stopTime,
                                            IntBuf sortedRecords,
                                            long totalCount,
                                            int diffCount) {
        MethodMetrics result = MethodMetrics.getInstance(methodTag, recorder.getMethodTagId(), startTime, stopTime);
        if (diffCount <= 0) {
            return result;
        }

        result.setTotalCount(totalCount);
        result.setMinTime(sortedRecords._getInt(0));
        result.setMaxTime(sortedRecords._getInt(sortedRecords.writerIndex() - 2));

        final long[] tpIndexArr = getTpIndexArr(totalCount);
        final int[] tpArr = result.getTpArr();
        int tpIndex = 0;
        long countMile = 0L;
        double sigma = 0.0D; //∑
        long totalTime = 0L;
        for (int i = 0, writerIdx = sortedRecords.writerIndex(); i < writerIdx; ) {
            final int timeCost = sortedRecords._getInt(i++);
            final int count = sortedRecords._getInt(i++);

            totalTime += (long) timeCost * count;
            countMile += count;

            while (tpIndex < tpIndexArr.length && countMile >= tpIndexArr[tpIndex]) {
                tpArr[tpIndex++] = timeCost;
            }

            sigma += count * Math.pow(timeCost, 2.0);
        }

        final double avgTime = ((double) totalTime) / totalCount;
        result.setAvgTime(avgTime);
        result.setStdDev(Math.sqrt((sigma / totalCount) - Math.pow(avgTime, 2.0)));
        result.setTotalTime(totalTime);
        result.setTotalTimePercent((double) totalTime / (stopTime - startTime));
        return reviseStatistic(result);
    }

    private static MethodMetrics reviseStatistic(MethodMetrics metrics) {
        final int[] tpArr = metrics.getTpArr();
        for (int i = 1, len = tpArr.length; i < len; ++i) {
            int last = tpArr[i - 1];
            int cur = tpArr[i];
            if (cur <= -1) {
                tpArr[i] = last;
            }
        }
        return metrics;
    }

    private static long[] getTpIndexArr(long totalCount) {
        final long[] result = LONG_ARR_TL.get();
        final double[] percentiles = MethodMetrics.getPercentiles();
        for (int i = 0, len = percentiles.length; i < len; ++i) {
            result[i] = getIndex(totalCount, percentiles[i]);
        }
        return result;
    }

    private static long getIndex(long totalCount, double percentile) {
        return (long) Math.ceil(totalCount * percentile);
    }
}
