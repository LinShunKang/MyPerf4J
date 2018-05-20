package cn.myperf4j.core.util;


import cn.myperf4j.base.PerfStats;

import java.util.List;

/**
 * Created by LinShunkang on 2018/3/30
 */
public final class PerfStatsFormatter {

    public static String getFormatStr(List<PerfStats> perfStatsList, long startMillis, long stopMillis) {
        int maxApiLength = getMaxApiLength(perfStatsList);

        String dataTitleFormat = "%-" + maxApiLength + "s%9s%9s%9s%9s%9s%10s%9s%9s%9s%9s%9s%9s%9s%9s%n";
        StringBuilder sb = new StringBuilder((perfStatsList.size() + 2) * (9 * 11 + 1 + maxApiLength));
        sb.append("MyPerf4J Performance Statistics [").append(DateUtils.getStr(startMillis)).append(", ").append(DateUtils.getStr(stopMillis)).append("]").append(String.format("%n"));
        sb.append(String.format(dataTitleFormat, "Api", "RPS", "Avg(ms)", "Min(ms)", "Max(ms)", "StdDev", "Count", "TP50", "TP90", "TP95", "TP99", "TP999", "TP9999", "TP99999", "TP100"));
        if (perfStatsList.isEmpty()) {
            return sb.toString();
        }

        String dataFormat = "%-" + maxApiLength + "s%9d%9.2f%9d%9d%9.2f%10d%9d%9d%9d%9d%9d%9d%9d%9d%n";
        for (int i = 0; i < perfStatsList.size(); ++i) {
            PerfStats perfStats = perfStatsList.get(i);
            sb.append(String.format(dataFormat,
                    perfStats.getApi(),
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

    private static int getMaxApiLength(List<PerfStats> perfStatsList) {
        int max = 0;
        for (int i = 0; i < perfStatsList.size(); ++i) {
            max = Math.max(max, perfStatsList.get(i).getApi().length());
        }
        return max;
    }

}
