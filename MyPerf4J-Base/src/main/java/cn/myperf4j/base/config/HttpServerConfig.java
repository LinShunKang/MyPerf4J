package cn.myperf4j.base.config;

import cn.myperf4j.base.util.Logger;

import static cn.myperf4j.base.config.MyProperties.getInt;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.ACCEPT_COUNT;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.MAX_WORKERS;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.MIN_WORKERS;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.PORT;

/**
 * Created by LinShunkang on 2020/09/13
 */
public class HttpServerConfig {

    private int port;

    private int minWorkers;

    private int maxWorkers;

    private int acceptCount;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMinWorkers() {
        return minWorkers;
    }

    public void setMinWorkers(int minWorkers) {
        this.minWorkers = minWorkers;
    }

    public int getMaxWorkers() {
        return maxWorkers;
    }

    public void setMaxWorkers(int maxWorkers) {
        this.maxWorkers = maxWorkers;
    }

    public int getAcceptCount() {
        return acceptCount;
    }

    public void setAcceptCount(int acceptCount) {
        this.acceptCount = acceptCount;
    }

    @Override
    public String toString() {
        return "HttpServerConfig{" +
                "port=" + port +
                ", minWorkers=" + minWorkers +
                ", maxWorkers=" + maxWorkers +
                ", acceptCount=" + acceptCount +
                '}';
    }

    public static HttpServerConfig loadHttpServerConfig() {
        Integer port = getInt(PORT);
        if (port == null) {
            port = 2048;
            Logger.info(PORT.key() + " is not configured, so use '2048' as default.");
        }

        HttpServerConfig config = new HttpServerConfig();
        config.setPort(port);
        config.setMinWorkers(getInt(MIN_WORKERS, 1));
        config.setMaxWorkers(getInt(MAX_WORKERS, 2));
        config.setAcceptCount(getInt(ACCEPT_COUNT, 1024));
        return config;
    }
}
