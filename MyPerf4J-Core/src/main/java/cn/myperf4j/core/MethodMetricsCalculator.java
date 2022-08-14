package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.buffer.LongBuf;
import cn.myperf4j.base.buffer.LongBufPool;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.core.recorder.Recorder;

import static cn.myperf4j.base.buffer.LongBuf.key;
import static cn.myperf4j.base.buffer.LongBuf.value;

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

    private static final LongBufPool longBufPool = LongBufPool.getInstance();

    private MethodMetricsCalculator() {
        //empty
    }

    public static MethodMetrics calMetrics(Recorder recorder, MethodTag methodTag, long startTime, long stopTime) {
        final int diffCount = recorder.getDiffCount();
        try (LongBuf longBuf = longBufPool.acquire(diffCount)) {
            final long totalCount = recorder.fillSortedRecords(longBuf);
            return calMetrics(recorder, methodTag, startTime, stopTime, longBuf, totalCount, diffCount);
        } catch (Exception e) {
            Logger.error("MethodMetricsCalculator.calMetrics(" + recorder + ", " + methodTag + ", "
                    + startTime + ", " + stopTime + ")", e);
        }
        return MethodMetrics.getInstance(methodTag, recorder.getMethodTagId(), startTime, stopTime);
    }

    private static MethodMetrics calMetrics(Recorder rec,
                                            MethodTag methodTag,
                                            long startTime,
                                            long stopTime,
                                            LongBuf sortedRecords,
                                            long totalCount,
                                            int diffCount) {
        final MethodMetrics result = MethodMetrics.getInstance(methodTag, rec.getMethodTagId(), startTime, stopTime);
        result.setTotalCount(totalCount);
        if (diffCount <= 0) {
            return result;
        }

        final long[] buf = sortedRecords._buf();
        result.setMinTime(key(buf[0]));
        result.setMaxTime(key(buf[sortedRecords.writerIndex() - 1]));

        final long[] tpIndexArr = getTpIndexArr(totalCount);
        final int tpIndexArrLen = tpIndexArr.length;
        final int[] tpArr = result.getTpArr();
        int tpIndex = 0, timeCost, count;
        double sigma = 0.0D; //∑
        long countMile = 0L, totalTime = 0L, kvLong;
        for (int i = 0, writerIdx = sortedRecords.writerIndex(); i < writerIdx; ) {
            kvLong = buf[i++];
            timeCost = key(kvLong);
            count = value(kvLong);

            totalTime += (long) timeCost * count;
            countMile += count;

            while (tpIndex < tpIndexArrLen && countMile >= tpIndexArr[tpIndex]) {
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
