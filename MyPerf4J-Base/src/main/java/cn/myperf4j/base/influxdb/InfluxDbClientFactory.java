package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.config.InfluxDbConfig;
import cn.myperf4j.base.config.ProfilingConfig;

/**
 * Created by LinShunkang on 2020/05/18
 */
public final class InfluxDbClientFactory {

    private static final InfluxDbConfig CONFIG = ProfilingConfig.influxDBConfig();

    private static final InfluxDbV1Client V1_CLIENT = new InfluxDbV1Client.Builder()
            .host(CONFIG.host())
            .port(CONFIG.port())
            .database(CONFIG.database())
            .username(CONFIG.username())
            .password(CONFIG.password())
            .connectTimeout(CONFIG.connectTimeout())
            .readTimeout(CONFIG.readTimeout())
            .build();

    private static final InfluxDbV2Client V2_CLIENT = new InfluxDbV2Client.Builder()
            .host(CONFIG.host())
            .port(CONFIG.port())
            .orgName(CONFIG.orgName())
            .database(CONFIG.database())
            .username(CONFIG.username())
            .password(CONFIG.password())
            .connectTimeout(CONFIG.connectTimeout())
            .readTimeout(CONFIG.readTimeout())
            .build();

    private InfluxDbClientFactory() {
        //empty
    }

    public static InfluxDbClient getClient() {
        return CONFIG.version().startsWith("2.") ? getV2Client() : getV1Client();
    }

    public static InfluxDbV1Client getV1Client() {
        return V1_CLIENT;
    }

    public static InfluxDbV2Client getV2Client() {
        return V2_CLIENT;
    }
}
