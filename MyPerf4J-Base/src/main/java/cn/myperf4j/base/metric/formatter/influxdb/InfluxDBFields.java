package cn.myperf4j.base.metric.formatter.influxdb;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2026/07/01
 */
interface InfluxDBFields {

    byte[] F_COUNT = "Count".getBytes(UTF_8);

    byte[] F_MEMORY_USED = "MemoryUsed".getBytes(UTF_8);

    byte[] F_MEMORY_CAPACITY = "MemoryCapacity".getBytes(UTF_8);

    byte[] F_HEAP_USED = "HeapUsed".getBytes(UTF_8);

    byte[] F_HEAP_USED_PERCENT = "HeapUsedPercent".getBytes(UTF_8);

    byte[] F_NON_HEAP_USED = "NonHeapUsed".getBytes(UTF_8);

    byte[] F_NON_HEAP_USED_PERCENT = "NonHeapUsedPercent".getBytes(UTF_8);

    byte[] F_PERM_GEN_USED = "PermGenUsed".getBytes(UTF_8);

    byte[] F_PERM_GEN_USED_PERCENT = "PermGenUsedPercent".getBytes(UTF_8);

    byte[] F_METASPACE_USED = "MetaspaceUsed".getBytes(UTF_8);

    byte[] F_METASPACE_USED_PERCENT = "MetaspaceUsedPercent".getBytes(UTF_8);

    byte[] F_CODE_CACHE_USED = "CodeCacheUsed".getBytes(UTF_8);

    byte[] F_CODE_CACHE_USED_PERCENT = "CodeCacheUsedPercent".getBytes(UTF_8);

    byte[] F_OLD_GEN_USED = "OldGenUsed".getBytes(UTF_8);

    byte[] F_OLD_GEN_USED_PERCENT = "OldGenUsedPercent".getBytes(UTF_8);

    byte[] F_EDEN_USED = "EdenUsed".getBytes(UTF_8);

    byte[] F_EDEN_USED_PERCENT = "EdenUsedPercent".getBytes(UTF_8);

    byte[] F_SURVIVOR_USED = "SurvivorUsed".getBytes(UTF_8);

    byte[] F_SURVIVOR_USED_PERCENT = "SurvivorUsedPercent".getBytes(UTF_8);

    byte[] F_INIT = "Init".getBytes(UTF_8);

    byte[] F_USED = "Used".getBytes(UTF_8);

    byte[] F_USED_PERCENT = "UsedPercent".getBytes(UTF_8);

    byte[] F_COMMITTED = "Committed".getBytes(UTF_8);

    byte[] F_MAX = "Max".getBytes(UTF_8);

    byte[] F_TOTAL_STARTED = "TotalStarted".getBytes(UTF_8);

    byte[] F_ACTIVE = "Active".getBytes(UTF_8);

    byte[] F_PEAK = "Peak".getBytes(UTF_8);

    byte[] F_DAEMON = "Daemon".getBytes(UTF_8);

    byte[] F_NEW = "New".getBytes(UTF_8);

    byte[] F_RUNNABLE = "Runnable".getBytes(UTF_8);

    byte[] F_BLOCKED = "Blocked".getBytes(UTF_8);

    byte[] F_WAITING = "Waiting".getBytes(UTF_8);

    byte[] F_TIMED_WAITING = "TimedWaiting".getBytes(UTF_8);

    byte[] F_TERMINATED = "Terminated".getBytes(UTF_8);

    byte[] F_LOADED = "Loaded".getBytes(UTF_8);

    byte[] F_UNLOADED = "Unloaded".getBytes(UTF_8);

    byte[] F_YOUNG_GC_COUNT = "YoungGcCount".getBytes(UTF_8);

    byte[] F_YOUNG_GC_TIME = "YoungGcTime".getBytes(UTF_8);

    byte[] F_AVG_YOUNG_GC_TIME = "AvgYoungGcTime".getBytes(UTF_8);

    byte[] F_FULL_GC_COUNT = "FullGcCount".getBytes(UTF_8);

    byte[] F_FULL_GC_TIME = "FullGcTime".getBytes(UTF_8);

    byte[] F_ZGC_TIME = "ZGcTime".getBytes(UTF_8);

    byte[] F_ZGC_COUNT = "ZGcCount".getBytes(UTF_8);

    byte[] F_AVG_ZGC_TIME = "AvgZGcTime".getBytes(UTF_8);

    byte[] F_ZGC_CYCLES_TIME = "ZGcCyclesTime".getBytes(UTF_8);

    byte[] F_ZGC_CYCLES_COUNT = "ZGcCyclesCount".getBytes(UTF_8);

    byte[] F_AVG_ZGC_CYCLES_TIME = "AvgZGcCyclesTime".getBytes(UTF_8);

    byte[] F_ZGC_PAUSES_TIME = "ZGcPausesTime".getBytes(UTF_8);

    byte[] F_ZGC_PAUSES_COUNT = "ZGcPausesCount".getBytes(UTF_8);

    byte[] F_AVG_ZGC_PAUSES_TIME = "AvgZGcPausesTime".getBytes(UTF_8);

    byte[] F_TOTAL = "Total".getBytes(UTF_8);

    byte[] F_TIME = "Time".getBytes(UTF_8);

    byte[] F_TOTAL_TIME = "TotalTime".getBytes(UTF_8);

    byte[] F_OPEN_COUNT = "OpenCount".getBytes(UTF_8);

    byte[] F_OPEN_PERCENT = "OpenPercent".getBytes(UTF_8);

    byte[] F_GC_COUNT = "GcCount".getBytes(UTF_8);

    byte[] F_GC_TIME = "GcTime".getBytes(UTF_8);

    byte[] F_AVG_GC_TIME = "AvgGcTime".getBytes(UTF_8);

    byte[] F_TOTAL_TIME_PERCENT = "TotalTimePercent".getBytes(UTF_8);

    byte[] F_RPS = "RPS".getBytes(UTF_8);

    byte[] F_AVG = "Avg".getBytes(UTF_8);

    byte[] F_MIN = "Min".getBytes(UTF_8);

    byte[] F_STD_DEV = "StdDev".getBytes(UTF_8);

    byte[] F_TP50 = "TP50".getBytes(UTF_8);

    byte[] F_TP90 = "TP90".getBytes(UTF_8);

    byte[] F_TP95 = "TP95".getBytes(UTF_8);

    byte[] F_TP99 = "TP99".getBytes(UTF_8);

    byte[] F_TP999 = "TP999".getBytes(UTF_8);

    byte[] F_TP9999 = "TP9999".getBytes(UTF_8);
}
