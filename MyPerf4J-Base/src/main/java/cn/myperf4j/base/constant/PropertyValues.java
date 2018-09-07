package cn.myperf4j.base.constant;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyValues {

    String DEFAULT_PRO_FILE = "/data/MyPerf4J/MyPerf4J.properties";

    String RECORDER_MODE_ACCURATE = "ACCURATE";

    String RECORDER_MODE_ROUGH = "ROUGH";

    int MIN_BACKUP_RECORDERS_COUNT = 1;

    int MAX_BACKUP_RECORDERS_COUNT = 8;

    int METRICS_PROCESS_TYPE_STDOUT = 0;

    int METRICS_PROCESS_TYPE_LOGGER = 1;

    int METRICS_PROCESS_TYPE_INFLUX_DB = 2;

    String LOG_ROLLING_TIME_MINUTELY = "MINUTELY";

    String LOG_ROLLING_TIME_HOURLY = "HOURLY";

    String LOG_ROLLING_TIME_DAILY = "DAILY";

    String DEFAULT_METRICS_FILE = "/data/logs/MyPerf4J/metrics.log";

    String NULL_FILE = "NULL";

    long DEFAULT_TIME_SLICE = 60 * 1000L;

    long MIN_TIME_SLICE = 1000L;

    long MAX_TIME_SLICE = 10 * 60 * 1000L;

    String FILTER_SEPARATOR = ";";

}
