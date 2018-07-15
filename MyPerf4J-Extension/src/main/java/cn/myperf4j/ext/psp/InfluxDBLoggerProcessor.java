package cn.myperf4j.ext.psp;

import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.PerfStats;
import cn.myperf4j.base.PerfStatsProcessor;
import cn.myperf4j.ext.util.NumFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by LinShunkang on 2018/7/9
 */

/**
 * 注意：
 * 1、该Processor只是利用PerfStats生成InfluxDB的LineProtocol，并把LineProtocol写到磁盘的指定文件
 * 2、该Processor把'类名'作为表名(Measurement)，'类名.方法名'为索引(Tag)，各个性能指标为列数据(Field)
 * <p>
 * 至于日志的收集和InfluxDB的写入，推荐使用官方的Telegraf(https://portal.influxdata.com/downloads)
 * 推荐在Telegraf配置文件中为不同的服务配置不同的数据库(Database)
 */
public class InfluxDBLoggerProcessor implements PerfStatsProcessor {

    private Logger logger = LoggerFactory.getLogger(InfluxDBLoggerProcessor.class);

    @Override
    public void process(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        if (perfStatsList == null || perfStatsList.isEmpty()) {
            return;
        }

        long startNanos = startMillis * 1000 * 1000L;
        for (int i = 0; i < perfStatsList.size(); ++i) {
            logger.info(createLineProtocol(perfStatsList.get(i), startNanos));
        }
    }

    private String createLineProtocol(PerfStats perfStats, long startNanos) {
        StringBuilder sb = new StringBuilder(getSuitSize(perfStats));
        sb.append(perfStats.getMethodTag().getClassName())
                .append(",Method=").append(perfStats.getMethodTag().getSimpleDesc())
                .append(" RPS=").append(perfStats.getRPS()).append("i")
                .append(",Avg=").append(NumFormatUtils.getFormatStr(perfStats.getAvgTime()))
                .append(",Min=").append(perfStats.getMinTime()).append("i")
                .append(",Max=").append(perfStats.getMaxTime()).append("i")
                .append(",StdDev=").append(NumFormatUtils.getFormatStr(perfStats.getStdDev()))
                .append(",Count=").append(perfStats.getTotalCount()).append("i")
                .append(",TP50=").append(perfStats.getTP50()).append("i")
                .append(",TP90=").append(perfStats.getTP90()).append("i")
                .append(",TP95=").append(perfStats.getTP95()).append("i")
                .append(",TP99=").append(perfStats.getTP99()).append("i")
                .append(",TP999=").append(perfStats.getTP999()).append("i")
                .append(",TP9999=").append(perfStats.getTP9999()).append("i")
                .append(",TP99999=").append(perfStats.getTP99999()).append("i")
                .append(",TP100=").append(perfStats.getTP100()).append("i")
                .append(" ").append(startNanos);
        return sb.toString();
    }

    private int getSuitSize(PerfStats perfStats) {
        MethodTag methodTag = perfStats.getMethodTag();
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
