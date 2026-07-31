package cn.myperf4j.base.config;

/**
 * Created by LinShunkang on 2026/07/05
 */
public final class InfluxDbV1Config extends InfluxDbConfig {

    private final String username;

    private final String password;

    public InfluxDbV1Config(String version,
                            String host,
                            int port,
                            String database,
                            int connectTimeout,
                            int readTimeout,
                            String username,
                            String password) {
        super(version, host, port, database, connectTimeout, readTimeout);
        this.username = username;
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public String toString() {
        return "InfluxDbV1Config{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
