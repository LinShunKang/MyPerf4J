package cn.myperf4j.base.config;

import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.StrUtils;

import java.util.List;

import static cn.myperf4j.base.config.MyProperties.getInt;
import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.ACCEPT_COUNT;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.MAX_WORKERS;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.MIN_WORKERS;
import static cn.myperf4j.base.constant.PropertyKeys.HttpServer.PORT;
import static cn.myperf4j.base.util.NumUtils.parseInt;
import static cn.myperf4j.base.util.StrUtils.isBlank;

/**
 * Created by LinShunkang on 2020/09/13
 */
public class HttpServerConfig {

    private int preferencePort;

    private int minPort;

    private int maxPort;

    private int minWorkers;

    private int maxWorkers;

    private int acceptCount;

    public int getPreferencePort() {
        return preferencePort;
    }

    public void setPreferencePort(int preferencePort) {
        this.preferencePort = preferencePort;
    }

    public int getMinPort() {
        return minPort;
    }

    public void setMinPort(int minPort) {
        this.minPort = minPort;
    }

    public int getMaxPort() {
        return maxPort;
    }

    public void setMaxPort(int maxPort) {
        this.maxPort = maxPort;
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
                "preferencePort=" + preferencePort +
                ", minPort=" + minPort +
                ", maxPort=" + maxPort +
                ", minWorkers=" + minWorkers +
                ", maxWorkers=" + maxWorkers +
                ", acceptCount=" + acceptCount +
                '}';
    }

    public static HttpServerConfig loadHttpServerConfig() {
        String portStr = getStr(PORT);
        if (isBlank(portStr)) {
            portStr = "2048,2000,2040";
            Logger.info(PORT.key() + " is not configured, so use '" + portStr + "' as default.");
        }

        final HttpServerConfig config = new HttpServerConfig();
        completePorts(config, portStr);
        config.setMinWorkers(getInt(MIN_WORKERS, 1));
        config.setMaxWorkers(getInt(MAX_WORKERS, 2));
        config.setAcceptCount(getInt(ACCEPT_COUNT, 1024));
        return config;
    }

    private static void completePorts(final HttpServerConfig config, final String portStr) {
        final List<String> ports = StrUtils.splitAsList(portStr, ',');
        if (ports.size() != 3) {
            config.setPreferencePort(parseInt(ports.get(0), 2048));
            config.setMinPort(2000);
            config.setMaxPort(2040);
            return;
        }

        config.setPreferencePort(parseInt(ports.get(0), 2048));
        config.setMinPort(parseInt(ports.get(1), 2000));
        config.setMaxPort(parseInt(ports.get(2), 2040));
    }
}
