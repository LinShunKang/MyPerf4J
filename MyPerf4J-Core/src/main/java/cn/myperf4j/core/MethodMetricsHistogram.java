package cn.myperf4j.core;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.util.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2019/07/23
 */
public class MethodMetricsHistogram {

    private static final ConcurrentHashMap<Integer, MethodMetricsInfo> methodMap = new ConcurrentHashMap<>(1024 * 8);

    public static void recordMetrics(MethodMetrics metrics) {
        MethodMetricsInfo methodMetricsInfo = methodMap.get(metrics.getMethodTagId());
        if (methodMetricsInfo != null) {
            methodMetricsInfo.add(metrics.getTP90(), metrics.getTP95(), metrics.getTP99());
            return;
        }

        MethodMetricsInfo info = new MethodMetricsInfo(metrics.getTP90(), metrics.getTP95(), metrics.getTP99());
        methodMap.put(metrics.getMethodTagId(), info);
    }

    public static void buildSysGenProfilingFile() {
        try {
            String file = ProfilingConfig.getInstance().getSysProfilingParamsFile();
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, false), 64 * 1024);

            MethodTagMaintainer tagMaintainer = MethodTagMaintainer.getInstance();
            Map<Integer, MethodMetricsInfo> methodMap = MethodMetricsHistogram.methodMap;
            for (Map.Entry<Integer, MethodMetricsInfo> entry : methodMap.entrySet()) {
                Integer methodId = entry.getKey();
                MethodMetricsInfo info = entry.getValue();
                if (info.getCount() <= 0) {
                    continue;
                }

                MethodTag methodTag = tagMaintainer.getMethodTag(methodId);
                fileWriter.write(methodTag.getFullDesc());
                fileWriter.write('=');
                fileWriter.write(calMostTimeThreshold(info) + ":" + 32);
                fileWriter.newLine();
            }
            fileWriter.flush();
        } catch (Exception e) {
            Logger.error("MethodMetricsHistogram.buildSysGenProfilingFile()", e);
        }
    }

    private static int calMostTimeThreshold(MethodMetricsInfo info) {
        int count = info.getCount();
        long tp99Avg = info.getTp99Sum() / count;
        if (tp99Avg <= 256) {
            return 256;
        } else if (tp99Avg <= 512) {
            return 512;
        } else if (tp99Avg <= 1024) {
            return 1024;
        } else if (tp99Avg <= 1536) {
            return 1536;
        } else if (tp99Avg <= 2048) {
            return 2048;
        }

        long tp95Avg = info.getTp95Sum() / count;
        if (tp95Avg <= 512) {
            return 512;
        } else if (tp95Avg <= 1024) {
            return 1024;
        } else if (tp95Avg <= 1536) {
            return 1536;
        } else if (tp95Avg <= 2048) {
            return 2048;
        }

        long tp90Avg = info.getTp90Sum() / count;
        if (tp90Avg <= 1024) {
            return 1024;
        } else if (tp90Avg <= 1536) {
            return 1536;
        } else if (tp90Avg <= 2048) {
            return 2048;
        }

        return 3000;
    }

}
