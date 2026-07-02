package cn.myperf4j.base.metric.formatter.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static cn.myperf4j.base.util.net.IpUtils.getLocalhostName;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2026/07/01
 */
interface InfluxDBValues {

    byte[] V_APP_NAME = ProfilingConfig.basicConfig().appName().getBytes(UTF_8);

    byte[] V_HOST = processTagOrField(getLocalhostName());
}
