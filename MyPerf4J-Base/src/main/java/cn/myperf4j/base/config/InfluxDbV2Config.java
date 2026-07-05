package cn.myperf4j.base.config;

/**
 * Created by LinShunkang on 2026/07/05
 */
public final class InfluxDbV2Config extends InfluxDbConfig {

    private final String orgName;

    private final String username;

    private final String password;

    public InfluxDbV2Config(String version,
                            String host,
                            int port,
                            String database,
                            int connectTimeout,
                            int readTimeout,
                            String orgName,
                            String username,
                            String password) {
        super(version, host, port, database, connectTimeout, readTimeout);
        this.orgName = orgName;
        this.username = username;
        this.password = password;
    }

    public String orgName() {
        return orgName;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    @Override
    public String toString() {
        return "InfluxDbV2Config{" +
                "orgName='" + orgName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
