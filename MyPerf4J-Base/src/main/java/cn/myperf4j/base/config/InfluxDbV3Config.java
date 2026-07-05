package cn.myperf4j.base.config;

/**
 * Created by LinShunkang on 2026/07/05
 */
public final class InfluxDbV3Config extends InfluxDbConfig {

    private final String token;

    public InfluxDbV3Config(String version,
                            String host,
                            int port,
                            String database,
                            int connectTimeout,
                            int readTimeout,
                            String token) {
        super(version, host, port, database, connectTimeout, readTimeout);
        this.token = token;
    }

    public String token() {
        return token;
    }

    @Override
    public String toString() {
        return "InfluxDbV3Config{" +
                "token='" + token + '\'' +
                "} " + super.toString();
    }
}
