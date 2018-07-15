package cn.myperf4j.ext.psp;

import cn.myperf4j.base.PerfStats;
import cn.myperf4j.base.PerfStatsProcessor;
import cn.myperf4j.base.psp.DefaultFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by LinShunkang on 2018/7/11
 */
public class DefaultLoggerProcessor implements PerfStatsProcessor {

    private Logger logger = LoggerFactory.getLogger(DefaultLoggerProcessor.class);

    @Override
    public void process(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        logger.info(DefaultFormatter.getFormatStr(perfStatsList, injectMethodCount, startMillis, stopMillis));
    }
}
