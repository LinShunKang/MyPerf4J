package cn.myperf4j.base.metric.processor.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.processor.AbstractMethodMetricsProcessor;
import cn.myperf4j.base.util.NumFormatUtils;

/**
 * Created by LinShunkang on 2018/7/9
 */

/**
 * 注意：
 * 1、该Processor只是利用MethodMetrics生成InfluxDB的LineProtocol，并把LineProtocol写到磁盘的指定文件
 * 2、该Processor把'类名'和'类名.方法名'为索引(Tag)，各个性能指标为列数据(Field)
 * <p>
 * 至于日志的收集和InfluxDB的写入，推荐使用官方的Telegraf(https://portal.influxdata.com/downloads)
 * 推荐在Telegraf配置文件中为不同的服务配置不同的数据库(Database)
 */
public class InfluxDBMethodMetricsProcessor extends AbstractMethodMetricsProcessor {

    private static final int MAX_LENGTH = 512;

    private ThreadLocal<StringBuilder> sbThreadLocal = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(256);
        }
    };

    @Override
    public void process(MethodMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = sbThreadLocal.get();
        try {
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(MethodMetrics methodMetrics, long startNanos, StringBuilder sb) {
        int suitSize = getSuitSize(methodMetrics);
        if (suitSize > MAX_LENGTH) {
            sb = new StringBuilder(suitSize);
        }

        sb.append("method_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(",ClassName=").append(methodMetrics.getMethodTag().getClassName())
                .append(",Method=").append(methodMetrics.getMethodTag().getSimpleDesc())
                .append(" RPS=").append(methodMetrics.getRPS()).append("i")
                .append(",Avg=").append(NumFormatUtils.getFormatStr(methodMetrics.getAvgTime()))
                .append(",Min=").append(methodMetrics.getMinTime()).append("i")
                .append(",Max=").append(methodMetrics.getMaxTime()).append("i")
                .append(",StdDev=").append(NumFormatUtils.getFormatStr(methodMetrics.getStdDev()))
                .append(",Count=").append(methodMetrics.getTotalCount()).append("i")
                .append(",TP50=").append(methodMetrics.getTP50()).append("i")
                .append(",TP90=").append(methodMetrics.getTP90()).append("i")
                .append(",TP95=").append(methodMetrics.getTP95()).append("i")
                .append(",TP99=").append(methodMetrics.getTP99()).append("i")
                .append(",TP999=").append(methodMetrics.getTP999()).append("i")
                .append(",TP9999=").append(methodMetrics.getTP9999()).append("i")
                .append(",TP99999=").append(methodMetrics.getTP99999()).append("i")
                .append(",TP100=").append(methodMetrics.getTP100()).append("i")
                .append(" ").append(startNanos);
        return sb.toString();
    }

    private int getSuitSize(MethodMetrics methodMetrics) {
        MethodTag methodTag = methodMetrics.getMethodTag();
        return methodTag.getClassName().length()
                + 8 + methodTag.getSimpleDesc().length()//Method
                + 5 + 6 + 1//RPS
                + 5 + 7 //Avg
                + 5 + 3 + 1//Min
                + 5 + 5 + 1//Max
                + 8 + 7//StdDev
                + 7 + 8 + 1//Count
                + 6 + 5 + 1//TP50
                + 6 + 5 + 1//TP90
                + 6 + 5 + 1//TP95
                + 6 + 5 + 1//TP99
                + 7 + 5 + 1//TP999
                + 8 + 5 + 1//TP9999
                + 9 + 5 + 1//TP99999
                + 7 + 5 + 1//TP100
                + 1 + 21//startNanos
                ;
    }

}
