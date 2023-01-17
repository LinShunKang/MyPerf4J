package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.config.InfluxDbConfig;
import cn.myperf4j.base.config.ProfilingConfig;

/**
 * Created by LinShunkang on 2020/05/18
 */
public final class InfluxDbClientFactory {

    private static final InfluxDbConfig CONFIG = ProfilingConfig.influxDBConfig();

    private static final InfluxDbClient CLIENT = generateClient();

    private static InfluxDbClient generateClient() {
        final String version = CONFIG.version();
        if (version.startsWith("2.")) {
            return new InfluxDbV2Client.Builder()
                    .host(CONFIG.host())
                    .port(CONFIG.port())
                    .orgName(CONFIG.orgName())
                    .database(CONFIG.database())
                    .username(CONFIG.username())
                    .password(CONFIG.password())
                    .connectTimeout(CONFIG.connectTimeout())
                    .readTimeout(CONFIG.readTimeout())
                    .build();
        } else {
            return new InfluxDbV1Client.Builder()
                    .host(CONFIG.host())
                    .port(CONFIG.port())
                    .database(CONFIG.database())
                    .username(CONFIG.username())
                    .password(CONFIG.password())
                    .connectTimeout(CONFIG.connectTimeout())
                    .readTimeout(CONFIG.readTimeout())
                    .build();
        }
    }

    public static InfluxDbClient getClient() {
        return CLIENT;
    }

    private InfluxDbClientFactory() {
        //empty
    }
}
