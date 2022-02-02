package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpRespStatus;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.http.client.HttpClient;
import cn.myperf4j.base.util.Base64;
import cn.myperf4j.base.util.Base64.Encoder;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.StrUtils;
import cn.myperf4j.base.util.concurrent.ExecutorManager;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.myperf4j.base.http.HttpStatusClass.INFORMATIONAL;
import static cn.myperf4j.base.http.HttpStatusClass.SUCCESS;
import static cn.myperf4j.base.util.concurrent.ThreadUtils.newThreadFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Created by LinShunkang on 2022/02/02
 */
public final class InfluxDbV2Client implements InfluxDbClient {

    private static final String API_SIGN_IN = "/api/v2/signin";

    private static final String API_SIGN_OUT = "/api/v2/signout";

    private static final Encoder BASE64_ENCODER = Base64.getEncoder();

    private static final ThreadPoolExecutor ASYNC_EXECUTOR = new ThreadPoolExecutor(
            1,
            2,
            3,
            MINUTES,
            new LinkedBlockingQueue<Runnable>(1024),
            newThreadFactory("MyPerf4J-InfluxDbV2Client_"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    static {
        ExecutorManager.addExecutorService(ASYNC_EXECUTOR);
    }

    private final String url;

    private final String writeReqUrl;

    private final String database;

    private final String username;

    private final String password;

    private final String authorization;

    private final HttpClient httpClient;

    private String cookie;

    public InfluxDbV2Client(Builder builder) {
        this.url = "http://" + builder.host + ":" + builder.port;
        this.writeReqUrl = buildWriteReqUrl(builder);
        this.database = builder.database;
        this.username = builder.username;
        this.password = builder.password;
        this.authorization = buildAuthorization(builder);
        this.httpClient = new HttpClient.Builder()
                .connectTimeout(builder.connectTimeout)
                .readTimeout(builder.readTimeout)
                .build();
        this.cookie = "";

        this.tryLogin();
        this.createDatabase();
    }

    private String buildWriteReqUrl(Builder builder) {
        return url + "/api/v2/write?org=" + builder.organized + "&bucket=" + builder.database + "&precision=ns";
    }

    private String buildAuthorization(Builder builder) {
        if (StrUtils.isNotBlank(builder.username) && StrUtils.isNotBlank(builder.password)) {
            String auth = username + ':' + password;
            return "Basic " + BASE64_ENCODER.encodeToString(auth.getBytes(UTF_8));
        }
        return "";
    }

    private boolean tryLogin() {
        if (StrUtils.isNotBlank(this.cookie)) {
            return true;
        }

        final HttpRequest req = new HttpRequest.Builder()
                .url(url + API_SIGN_IN)
                .header("Authorization", authorization)
                .post(" ")
                .build();
        try {
            final HttpResponse response = httpClient.execute(req);
            Logger.info("InfluxDbV2Client login response.status=" + response.getStatus());

            if (response.getStatus().statusClass() == SUCCESS) {
                this.cookie = response.getHeaders().get("Set-Cookie");
                return true;
            }
        } catch (IOException e) {
            Logger.error("InfluxDbV2Client.tryLogin(): e=" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean createDatabase() {
        if (!tryLogin()) {
            Logger.warn("try login fails, so do not continue create database!");
            return false;
        }

        final HttpRequest req = new HttpRequest.Builder()
                .url(url + "/query")
                .header("Cookie", cookie)
                .post("q=CREATE DATABASE " + database)
                .build();
        try {
            final HttpResponse response = httpClient.execute(req);
            Logger.info("InfluxDbV2Client create database '" + database + "' response.status=" + response.getStatus());

            if (response.getStatus().statusClass() == SUCCESS) {
                return true;
            }
        } catch (IOException e) {
            Logger.error("InfluxDbV2Client.createDatabase(): e=" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean writeMetricsAsync(final String content) {
        if (StrUtils.isBlank(content)) {
            return false;
        }

        try {
            ASYNC_EXECUTOR.execute(new ReqTask(content));
            return true;
        } catch (Throwable t) {
            Logger.error("InfluxDbV2Client.writeMetricsAsync(): t=" + t.getMessage(), t);
        }
        return false;
    }

    private class ReqTask implements Runnable {

        private final String content;

        ReqTask(String content) {
            this.content = content;
        }

        @Override
        public void run() {
            if (!tryLogin()) {
                Logger.warn("try login fails, so do not continue write content!");
                return;
            }

            final HttpRequest req = new HttpRequest.Builder()
                    .url(writeReqUrl)
                    .header("Cookie", cookie)
                    .post(content)
                    .build();
            try {
                final HttpResponse response = httpClient.execute(req);
                final HttpRespStatus status = response.getStatus();
                if (status.statusClass() == SUCCESS && Logger.isDebugEnable()) {
                    Logger.debug("ReqTask.run(): respStatus=" + status.simpleString() + ", reqBody=" + content);
                } else if (status.statusClass() != INFORMATIONAL && status.statusClass() != SUCCESS) {
                    Logger.warn("ReqTask.run(): respStatus=" + status.simpleString() + ", reqBody=" + content);
                }
            } catch (IOException e) {
                Logger.warn("ReqTask.run() catch IOException: " + e.getMessage());
            } catch (Throwable t) {
                Logger.error("ReqTask.run() catch Exception!", t);
            }
        }
    }

    public static class Builder {

        private static final int DEFAULT_CONNECT_TIMEOUT = 3000;

        private static final int DEFAULT_READ_TIMEOUT = 5000;

        private String host;

        private int port;

        private String organized;

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

        public String organized() {
            return organized;
        }

        public Builder organized(String organized) {
            this.organized = organized;
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

        public InfluxDbV2Client build() {
            return new InfluxDbV2Client(this);
        }
    }
}
