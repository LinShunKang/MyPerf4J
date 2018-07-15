package cn.myperf4j.core.constant;

import cn.myperf4j.base.psp.DefaultStdoutProcessor;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyValues {

    String DEFAULT_PRO_FILE = "/data/MyPerf4J/MyPerf4J.properties";

    String RECORDER_MODE_ACCURATE = "accurate";

    String RECORDER_MODE_ROUGH = "rough";

    int MIN_BACKUP_RECORDERS_COUNT = 1;

    int MAX_BACKUP_RECORDERS_COUNT = 8;

    String DEFAULT_PERF_STATS_PROCESSOR = DefaultStdoutProcessor.class.getName();

    long DEFAULT_TIME_SLICE = 60 * 1000L;

    long MIN_TIME_SLICE = 1000L;

    long MAX_TIME_SLICE = 10 * 60 * 1000L;

    String FILTER_SEPARATOR = ";";

}
