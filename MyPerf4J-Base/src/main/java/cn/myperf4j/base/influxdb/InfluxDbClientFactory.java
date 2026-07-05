package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.config.InfluxDbConfig;
import cn.myperf4j.base.config.InfluxDbV1Config;
import cn.myperf4j.base.config.InfluxDbV2Config;
import cn.myperf4j.base.config.InfluxDbV3Config;
import cn.myperf4j.base.config.ProfilingConfig;

/**
 * Created by LinShunkang on 2020/05/18
 */
public final class InfluxDbClientFactory {

    private static final InfluxDbClient CLIENT = generateClient();

    private static InfluxDbClient generateClient() {
        final InfluxDbConfig config = ProfilingConfig.influxDBConfig();
        if (config instanceof InfluxDbV3Config) {
            return generateV3Client((InfluxDbV3Config) config);
        } else if (config instanceof InfluxDbV2Config) {
            return generateV2Client((InfluxDbV2Config) config);
        } else {
            return generateV1Client((InfluxDbV1Config) config);
        }
    }

    private static InfluxDbV3Client generateV3Client(InfluxDbV3Config config) {
        return new InfluxDbV3Client.Builder()
                .host(config.host())
                .port(config.port())
                .database(config.database())
                .token(config.token())
                .connectTimeout(config.connectTimeout())
                .readTimeout(config.readTimeout())
                .build();
    }

    private static InfluxDbV2Client generateV2Client(InfluxDbV2Config config) {
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

    private static InfluxDbClient generateV1Client(InfluxDbV1Config config) {
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
