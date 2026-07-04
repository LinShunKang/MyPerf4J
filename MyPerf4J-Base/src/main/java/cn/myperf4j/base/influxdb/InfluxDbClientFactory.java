package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.config.InfluxDbConfig;
import cn.myperf4j.base.config.ProfilingConfig;

/**
 * Created by LinShunkang on 2020/05/18
 */
public final class InfluxDbClientFactory {

    private static final InfluxDbClient CLIENT = generateClient();

    private static InfluxDbClient generateClient() {
        final InfluxDbConfig config = ProfilingConfig.influxDBConfig();
        final String version = config.version();
        if (version.startsWith("3.")) {
            return generateV3Client(config);
        } else if (version.startsWith("2.")) {
            return generateV2Client(config);
        } else {
            return generateV1Client(config);
        }
    }

    private static InfluxDbV3Client generateV3Client(InfluxDbConfig config) {
        return new InfluxDbV3Client.Builder()
                .host(config.host())
                .port(config.port())
                .database(config.database())
                .token(config.token())
                .connectTimeout(config.connectTimeout())
                .readTimeout(config.readTimeout())
                .build();
    }

    private static InfluxDbV2Client generateV2Client(InfluxDbConfig config) {
        return new InfluxDbV2Client.Builder()
                .host(config.host())
                .port(config.port())
                .orgName(config.orgName())
                .database(config.database())
                .username(config.username())
                .password(config.password())
                .connectTimeout(config.connectTimeout())
                .readTimeout(config.readTimeout())
                .build();
    }

    private static InfluxDbClient generateV1Client(InfluxDbConfig config) {
        return new InfluxDbV1Client.Builder()
                .host(config.host())
                .port(config.port())
                .database(config.database())
                .username(config.username())
                .password(config.password())
                .connectTimeout(config.connectTimeout())
                .readTimeout(config.readTimeout())
                .build();
    }

    public static InfluxDbClient getClient() {
        return CLIENT;
    }

    private InfluxDbClientFactory() {
        //empty
    }
}
