package cn.perf4j;

/**
 * Created by LinShunkang on 2018/4/9
 */
public interface PropConstants {

    String PRO_FILE_NAME = "config/myPerf4J.properties";

    String PERF_STATS_PROCESSOR = "MyPerf4J.PSP";

    String RECORDER_MODE = "MyPerf4J.RecMode";

    String RECORDER_MODE_ACCURATE = "accurate";

    String MILL_TIME_SLICE = "MyPerf4J.MillTimeSlice";

    long DEFAULT_TIME_SLICE = 60 * 1000L;

    long MIN_TIME_SLICE = 10 * 1000L;

    long MAX_TIME_SLICE = 10 * 60 * 1000L;

    String RUNNING_STATUS = "RUNNING";

    String RUNNING_STATUS_YES = "YES";

    String RUNNING_STATUS_NO = "NO";

}
