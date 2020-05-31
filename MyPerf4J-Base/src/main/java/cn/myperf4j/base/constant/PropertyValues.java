package cn.myperf4j.base.constant;

/**
 * Created by LinShunkang on 2018/4/27
 */
public interface PropertyValues {

    String DEFAULT_PRO_FILE = "/data/MyPerf4J/MyPerf4J.properties";

    interface LegacyValues {

        int METRICS_PROCESS_TYPE_STDOUT = 0;

        int METRICS_PROCESS_TYPE_LOGGER = 1;

        int METRICS_PROCESS_TYPE_INFLUX_DB = 2;
    }

    interface Metrics {

        String EXPORTER_LOG_STDOUT = "log.stdout";

        String EXPORTER_LOG_STANDARD = "log.standard";

        String EXPORTER_LOG_INFLUX_DB = "log.influxdb";

        String EXPORTER_HTTP_INFLUX_DB = "http.influxdb";

        String STDOUT_METRICS_FILE = "STDOUT";

        String DEFAULT_METRICS_FILE = "/data/logs/MyPerf4J/metrics.log";

        String NULL_FILE = "NULL";

        String STDOUT_FILE = "STDOUT";

        long DEFAULT_TIME_SLICE = 60 * 1000L;

        long MIN_TIME_SLICE = 1000L;

        long MAX_TIME_SLICE = 10 * 60 * 1000L;

        int DEFAULT_LOG_RESERVE_COUNT = 7;

        String LOG_ROLLING_MINUTELY = "MINUTELY";

        String LOG_ROLLING_HOURLY = "HOURLY";

        String LOG_ROLLING_DAILY = "DAILY";

    }

    interface Recorder {

        String MODE_ACCURATE = "ACCURATE";

        String MODE_ROUGH = "ROUGH";

        int MIN_BACKUP_RECORDERS_COUNT = 1;

        int MAX_BACKUP_RECORDERS_COUNT = 8;

    }

    interface Separator {

        char ELE = ';';

        char ELE_KV = ':';

        char ARR_ELE = ',';
    }

}
