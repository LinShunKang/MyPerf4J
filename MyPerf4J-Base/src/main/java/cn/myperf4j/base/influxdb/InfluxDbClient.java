package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.http.client.HttpClient;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpRespStatus;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.util.Base64;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.MapUtils;
import cn.myperf4j.base.util.StrUtils;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.myperf4j.base.http.HttpStatusClass.INFORMATIONAL;
import static cn.myperf4j.base.http.HttpStatusClass.SUCCESS;
import static cn.myperf4j.base.util.ThreadUtils.newThreadFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2020/05/18
 */
public final class InfluxDbClient {

    private static final ThreadPoolExecutor ASYNC_EXECUTOR = new ThreadPoolExecutor(
            1,
            2,
            3,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<Runnable>(1024),
            newThreadFactory("MyPerf4J-InfluxDbClient_"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private final String host;

    private final int port;

    private final String database;

    private final String username;

    private final String password;

    private final String authorization;

    protected final HttpClient httpClient;

    public InfluxDbClient(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;
        this.authorization = buildAuthorization(builder);
        this.httpClient = new HttpClient.Builder()
                .connectTimeout(builder.connectTimeout)
                .readTimeout(builder.readTimeout)
                .build();
    }

    private String buildAuthorization(Builder builder) {
        if (StrUtils.isNotBlank(builder.username) && StrUtils.isNotBlank(builder.password)) {
            String auth = username + ':' + password;
            return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(UTF_8));
        }
        return "";
    }

    public boolean createDatabase() {
        HttpRequest req = new HttpRequest.Builder()
                .remoteHost(host)
                .remotePort(port)
                .path("/query")
                .header("Authorization", authorization)
                .post("q=CREATE DATABASE " + database)
                .build();
        try {
            HttpResponse response = httpClient.execute(req);
            Logger.info("InfluxDbClient create database '" + database + "' response.status=" + response.getStatus());

            if (response.getStatus().statusClass() == SUCCESS) {
                return true;
            }
        } catch (IOException e) {
            Logger.error("InfluxDbClient.createDatabase(): e=" + e.getMessage(), e);
        }
        return false;
    }

    public boolean writeMetricsAsync(final String content) {
        try {
            ASYNC_EXECUTOR.execute(new ReqTask(content));
            return true;
        } catch (Throwable t) {
            Logger.error("InfluxDbClient.writeMetricsAsync(): t=" + t.getMessage(), t);
        }
        return false;
    }

    private class ReqTask implements Runnable {

        private final String content;

        public ReqTask(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            HttpRequest req = new HttpRequest.Builder()
                    .remoteHost(host)
                    .remotePort(port)
                    .path("/write")
                    .header("Authorization", authorization)
                    .params(MapUtils.of("db", singletonList(database)))
                    .post(content)
                    .build();
            try {
                HttpResponse response = httpClient.execute(req);
                HttpRespStatus status = response.getStatus();
                if (status.statusClass() == SUCCESS && Logger.isDebugEnable()) {
                    Logger.debug("ReqTask.run(): respStatus=" + status.simpleString() + ", reqBody=" + content);
                } else if (status.statusClass() != INFORMATIONAL && status.statusClass() != SUCCESS) {
                    Logger.warn("ReqTask.run(): respStatus=" + status.simpleString() + ", reqBody=" + content);
                }
            } catch (IOException e) {
                Logger.error("ReqTask.run(): ", e);
            }
        }
    }

    public static class Builder {

        private static final int DEFAULT_CONNECT_TIMEOUT = 1000;

        private static final int DEFAULT_READ_TIMEOUT = 3000;

        private String host;

        private int port;

        private String database;

        private String username;

        private String password;

        private int connectTimeout;

        private int readTimeout;

        public Builder() {
            this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
            this.readTimeout = DEFAULT_READ_TIMEOUT;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder database(String database) {
            this.database = database;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public InfluxDbClient build() {
            return new InfluxDbClient(this);
        }
    }

}
