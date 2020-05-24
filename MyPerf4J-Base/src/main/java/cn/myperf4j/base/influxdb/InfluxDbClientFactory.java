package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingConfig.InfluxDBConfig;

/**
 * Created by LinShunkang on 2020/05/18
 */
public final class InfluxDbClientFactory {

    private static final InfluxDBConfig CONFIG = ProfilingConfig.getInstance().influxDBConfig();

    private static final InfluxDbClient CLIENT = new InfluxDbClient.Builder()
            .host(CONFIG.host())
            .port(CONFIG.port())
            .database(CONFIG.database())
            .username(CONFIG.username())
            .password(CONFIG.password())
            .connectTimeout(CONFIG.connectTimeout())
            .readTimeout(CONFIG.readTimeout())
            .build();

    public static InfluxDbClient getClient() {
        return CLIENT;
    }


}
