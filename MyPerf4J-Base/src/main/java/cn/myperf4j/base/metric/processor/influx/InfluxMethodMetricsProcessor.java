package cn.myperf4j.base.metric.processor.influx;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.MethodMetrics;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.metric.processor.AbstractMethodMetricsProcessor;
import cn.myperf4j.base.util.LineProtocolUtils;
import cn.myperf4j.base.util.NumFormatUtils;

/**
 * Created by LinShunkang on 2018/7/9
 */

/**
 * 注意：
 * 1、该 Processor 只是利用 MethodMetrics 生成 InfluxDB 的 LineProtocol，并把 LineProtocol 写到磁盘的指定文件
 * 2、该 Processor 把'类名'和'类名.方法名'为索引(Tag)，各个性能指标为列数据(Field)
 * <p>
 * 至于日志的收集和 InfluxDB 的写入，推荐使用官方的 Telegraf(https://portal.influxdata.com/downloads)
 * 推荐在 Telegraf 配置文件中为不同的服务配置不同的数据库(Database)
 */
public class InfluxMethodMetricsProcessor extends AbstractMethodMetricsProcessor {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(256);
        }
    };

    @Override
    public void process(MethodMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(MethodMetrics methodMetrics, long startNanos, StringBuilder sb) {
        MethodTag methodTag = methodMetrics.getMethodTag();
        String methodDesc = LineProtocolUtils.processTagOrField(methodTag.getSimpleDesc());
        sb.append("method_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(",ClassName=").append(methodTag.getSimpleClassName())
                .append(",Method=").append(methodDesc)
                .append(",Type=").append(methodTag.getType())
                .append(",Level=").append(methodTag.getLevel())
                .append(" TotalTimePercent=").append(methodMetrics.getTotalTimePercent())
                .append(",RPS=").append(methodMetrics.getRPS()).append('i')
                .append(",Avg=").append(NumFormatUtils.doubleFormat(methodMetrics.getAvgTime()))
                .append(",Min=").append(methodMetrics.getMinTime()).append('i')
                .append(",Max=").append(methodMetrics.getMaxTime()).append('i')
                .append(",StdDev=").append(NumFormatUtils.doubleFormat(methodMetrics.getStdDev()))
                .append(",Count=").append(methodMetrics.getTotalCount()).append('i')
                .append(",TP50=").append(methodMetrics.getTP50()).append('i')
                .append(",TP90=").append(methodMetrics.getTP90()).append('i')
                .append(",TP95=").append(methodMetrics.getTP95()).append('i')
                .append(",TP99=").append(methodMetrics.getTP99()).append('i')
                .append(",TP999=").append(methodMetrics.getTP999()).append('i')
                .append(",TP9999=").append(methodMetrics.getTP9999()).append('i')
                .append(' ').append(startNanos);
        return sb.toString();
    }

}
