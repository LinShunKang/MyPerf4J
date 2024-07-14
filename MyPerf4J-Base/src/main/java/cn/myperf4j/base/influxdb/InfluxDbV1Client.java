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
import static cn.myperf4j.base.util.StrUtils.isNotBlank;
import static cn.myperf4j.base.util.concurrent.ThreadUtils.newThreadFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Created by LinShunkang on 2020/05/18
 */
public final class InfluxDbV1Client implements InfluxDbClient {

    private static final Encoder BASE64_ENCODER = Base64.getEncoder();

    private static final ThreadPoolExecutor ASYNC_EXECUTOR = new ThreadPoolExecutor(
            1,
            2,
            3,
            MINUTES,
            new LinkedBlockingQueue<Runnable>(1024),
            newThreadFactory("MyPerf4J-InfluxDbV1Client_"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    static {
        ExecutorManager.addExecutorService(ASYNC_EXECUTOR);
    }

    private final String writeReqUrl;

    private final String authorization;

    private final HttpClient httpClient;

    public InfluxDbV1Client(Builder builder) {
        this.writeReqUrl = "http://" + builder.host + ":" + builder.port + "/write?db=" + builder.database;
        this.authorization = buildAuthorization(builder);
        this.httpClient = new HttpClient.Builder()
                .connectTimeout(builder.connectTimeout)
                .readTimeout(builder.readTimeout)
                .build();
    }

    private String buildAuthorization(Builder builder) {
        if (isNotBlank(builder.username) && isNotBlank(builder.password)) {
            final String auth = builder.username + ':' + builder.password;
            return "Basic " + BASE64_ENCODER.encodeToString(auth.getBytes(UTF_8));
        }
        return "";
    }

    @Override
    public boolean writeMetricsSync(String content) {
        final HttpRequest req = new HttpRequest.Builder()
                .url(writeReqUrl)
                .header("Authorization", authorization)
                .post(content)
                .build();
        try {
            final HttpResponse response = httpClient.execute(req);
            final HttpRespStatus status = response.getStatus();
            if (status.statusClass() == SUCCESS) {
                if (Logger.isDebugEnable()) {
                    Logger.debug("InfluxDbV1Client.writeMetricsSync(): respStatus=" + status.simpleString()
                            + ", reqBody=" + content);
                }
                return true;
            }

            if (status.statusClass() != INFORMATIONAL && status.statusClass() != SUCCESS) {
                Logger.warn("InfluxDbV1Client.writeMetricsSync(): respStatus=" + status.simpleString()
                        + ", reqBody=" + content);
            }
        } catch (IOException e) {
            Logger.warn("InfluxDbV1Client.writeMetricsSync() catch IOException!", e);
        } catch (Throwable t) {
            Logger.error("InfluxDbV1Client.writeMetricsSync() catch Exception!", t);
        }
        return false;
    }

    @Override
    public boolean writeMetricsAsync(final String content) {
        if (StrUtils.isBlank(content)) {
            return false;
        }

        try {
            ASYNC_EXECUTOR.execute(() -> writeMetricsSync(content));
            return true;
        } catch (Throwable t) {
            Logger.error("InfluxDbV1Client.writeMetricsAsync(): t=" + t.getMessage(), t);
        }
        return false;
    }

    @Override
    public boolean close() {
        return true; //do nothing.
    }

    public static class Builder {

        private static final int DEFAULT_CONNECT_TIMEOUT = 3000;

        private static final int DEFAULT_READ_TIMEOUT = 5000;

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

        public InfluxDbV1Client build() {
            return new InfluxDbV1Client(this);
        }
    }
}
