package cn.myperf4j.base.metric.formatter.influxdb;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2026/07/01
 */
interface InfluxDBTags {

    byte[] T_APP_NAME = "AppName".getBytes(UTF_8);

    byte[] T_POOL_NAME = "PoolName".getBytes(UTF_8);

    byte[] T_HOST = "host".getBytes(UTF_8);

    byte[] T_GC_NAME = "GcName".getBytes(UTF_8);

    byte[] T_CLASS_NAME = "ClassName".getBytes(UTF_8);

    byte[] T_METHOD = "Method".getBytes(UTF_8);

    byte[] T_TYPE = "Type".getBytes(UTF_8);

    byte[] T_LEVEL = "Level".getBytes(UTF_8);
}
