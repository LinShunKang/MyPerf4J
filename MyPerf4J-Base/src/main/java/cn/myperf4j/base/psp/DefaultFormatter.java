package cn.myperf4j.base.psp;

import cn.myperf4j.base.PerfStats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by LinShunkang on 2018/3/30
 */
public final class DefaultFormatter {

    private static final ThreadLocal<DateFormat> DEFAULT_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static String getFormatStr(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        int[] statisticsArr = getStatistics(perfStatsList);
        int maxApiLength = statisticsArr[0];

        String dataTitleFormat = "%-" + maxApiLength + "s%9s%9s%9s%9s%9s%10s%9s%9s%9s%9s%9s%9s%9s%9s%n";
        StringBuilder sb = new StringBuilder((perfStatsList.size() + 2) * (9 * 11 + 1 + maxApiLength));
        sb.append("MyPerf4J Performance Statistics [").append(getStr(startMillis)).append(", ").append(getStr(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "Method[" + perfStatsList.size() + "/" + injectMethodCount + "]", "RPS", "Avg(ms)", "Min(ms)", "Max(ms)", "StdDev", "Count", "TP50", "TP90", "TP95", "TP99", "TP999", "TP9999", "TP99999", "TP100"));
        if (perfStatsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-" + maxApiLength + "s%9d%9.2f%9d%9d%9.2f%10d%9d%9d%9d%9d%9d%9d%9d%9d%n";
        for (int i = 0; i < perfStatsList.size(); ++i) {
            PerfStats perfStats = perfStatsList.get(i);
            if (perfStats.getTotalCount() <= 0) {
                continue;
            }

            sb.append(String.format(dataFormat,
                    perfStats.getMethodTag().getSimpleDesc(),
                    perfStats.getRPS(),
                    perfStats.getAvgTime(),
                    perfStats.getMinTime(),
                    perfStats.getMaxTime(),
                    perfStats.getStdDev(),
                    perfStats.getTotalCount(),
                    perfStats.getTP50(),
                    perfStats.getTP90(),
                    perfStats.getTP95(),
                    perfStats.getTP99(),
                    perfStats.getTP999(),
                    perfStats.getTP9999(),
                    perfStats.getTP99999(),
                    perfStats.getTP100()));
        }
        return sb.toString();
    }

    /**
     * @param perfStatsList
     * @return : int[0]:max(api.length)
     */
    private static int[] getStatistics(List<PerfStats> perfStatsList) {
        int[] result = {1};
        for (int i = 0; i < perfStatsList.size(); ++i) {
            PerfStats stats = perfStatsList.get(i);
            if (stats == null || stats.getMethodTag() == null) {
                continue;
            }

            result[0] = Math.max(result[0], stats.getMethodTag().getSimpleDesc().length());
        }
        return result;
    }

    private static String getStr(long millis) {
        return DEFAULT_DATE_FORMAT.get().format((new Date(millis)));
    }
}
