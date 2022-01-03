package cn.myperf4j.base.config;

import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.StrUtils;

import static cn.myperf4j.base.config.MyProperties.getInt;
import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.CONN_TIMEOUT;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.DATABASE;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.HOST;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.PASSWORD;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.PORT;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.READ_TIMEOUT;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.USERNAME;
import static cn.myperf4j.base.util.StrUtils.trimToEmpty;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class InfluxDbConfig {

    private String host;

    private int port;

    private String database;

    private int connectTimeout;

    private int readTimeout;

    private String username;

    private String password;

    public String host() {
        return host;
    }

    public void host(String host) {
        this.host = host;
    }

    public int port() {
        return port;
    }

    public void port(int port) {
        this.port = port;
    }

    public String database() {
        return database;
    }

    public void database(String database) {
        this.database = database;
    }

    public int connectTimeout() {
        return connectTimeout;
    }

    public void connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int readTimeout() {
        return readTimeout;
    }

    public void readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String username() {
        return username;
    }

    public void username(String username) {
        this.username = username;
    }

    public String password() {
        return password;
    }

    public void password(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "InfluxDbConfig{" +
                "ip='" + host + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                ", connectTimeout=" + connectTimeout +
                ", readTimeout=" + readTimeout +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static InfluxDbConfig loadInfluxDbConfig() {
        String host = trimToEmpty(getStr(HOST));
        if (StrUtils.isBlank(host)) {
            host = "127.0.0.1";
            Logger.info(HOST.key() + " is not configured, so use '127.0.0.1' as default host.");
        }

        Integer port = getInt(PORT);
        if (port == null) {
            port = 8086;
            Logger.info(PORT.key() + " is not configured, so use '8086' as default port.");
        }

        final InfluxDbConfig config = new InfluxDbConfig();
        config.host(host);
        config.port(port);
        config.database(trimToEmpty(getStr(DATABASE)));
        config.username(trimToEmpty(getStr(USERNAME)));
        config.password(trimToEmpty(getStr(PASSWORD)));
        config.connectTimeout(getInt(CONN_TIMEOUT, 3000));
        config.readTimeout(getInt(READ_TIMEOUT, 5000));
        return config;
    }
}
