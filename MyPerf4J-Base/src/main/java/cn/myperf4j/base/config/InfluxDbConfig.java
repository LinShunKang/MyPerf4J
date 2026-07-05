package cn.myperf4j.base.config;

import cn.myperf4j.base.util.Logger;

import static cn.myperf4j.base.config.MyProperties.getInt;
import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.CONN_TIMEOUT;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.DATABASE;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.HOST;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.ORG_NAME;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.PASSWORD_V1;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.PASSWORD_V2;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.PORT;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.READ_TIMEOUT;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.TOKEN;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.USERNAME_V1;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.USERNAME_V2;
import static cn.myperf4j.base.constant.PropertyKeys.InfluxDB.VERSION;
import static cn.myperf4j.base.util.StrUtils.isBlank;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class InfluxDbConfig {

    private final String version;

    private final String host;

    private final int port;

    private final String database;

    private final int connectTimeout;

    private final int readTimeout;

    public InfluxDbConfig(String version, String host, int port, String database, int connectTimeout, int readTimeout) {
        this.version = version;
        this.host = host;
        this.port = port;
        this.database = database;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public String version() {
        return version;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String database() {
        return database;
    }

    public int connectTimeout() {
        return connectTimeout;
    }

    public int readTimeout() {
        return readTimeout;
    }

    @Override
    public String toString() {
        return "InfluxDbConfig{" +
                "version='" + version + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", database='" + database + '\'' +
                ", connectTimeout=" + connectTimeout +
                ", readTimeout=" + readTimeout +
                '}';
    }

    public static InfluxDbConfig loadInfluxDbConfig() {
        String version = getStr(VERSION);
        if (isBlank(version)) {
            version = "1.x";
            Logger.info(VERSION.key() + " is not configured, so use '1.x' as default version.");
        }

        String host = getStr(HOST);
        if (isBlank(host)) {
            host = "127.0.0.1";
            Logger.info(HOST.key() + " is not configured, so use '127.0.0.1' as default host.");
        }

        Integer port = getInt(PORT);
        if (port == null) {
            port = 8086;
            Logger.info(PORT.key() + " is not configured, so use '8086' as default port.");
        }

        final String db = getStr(DATABASE);
        final int connTimeout = getInt(CONN_TIMEOUT, 3000);
        final int readTimeout = getInt(READ_TIMEOUT, 5000);
        if (version.startsWith("3.")) {
            return loadV3Config(version, host, port, db, connTimeout, readTimeout);
        } else if (version.startsWith("2.")) {
            return loadV2Config(version, host, port, db, connTimeout, readTimeout);
        } else {
            return loadV1Config(version, host, port, db, connTimeout, readTimeout);
        }
    }

    private static InfluxDbV3Config loadV3Config(String v,
                                                 String host,
                                                 int port,
                                                 String db,
                                                 int connTimeout,
                                                 int readTimeout) {
        return new InfluxDbV3Config(v, host, port, db, connTimeout, readTimeout, getStr(TOKEN));
    }

    private static InfluxDbV2Config loadV2Config(String v,
                                                 String host,
                                                 int port,
                                                 String db,
                                                 int connTimeout,
                                                 int readTimeout) {
        final String orgName = getStr(ORG_NAME);
        final String username = getStr(USERNAME_V2);
        final String password = getStr(PASSWORD_V2);
        return new InfluxDbV2Config(v, host, port, db, connTimeout, readTimeout, orgName, username, password);
    }

    private static InfluxDbV1Config loadV1Config(String v,
                                                 String host,
                                                 int port,
                                                 String db,
                                                 int connTimeout,
                                                 int readTimeout) {
        final String username = getStr(USERNAME_V1);
        final String password = getStr(PASSWORD_V1);
        return new InfluxDbV1Config(v, host, port, db, connTimeout, readTimeout, username, password);
    }
}
