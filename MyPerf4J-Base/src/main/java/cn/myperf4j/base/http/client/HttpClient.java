package cn.myperf4j.base.http.client;

import cn.myperf4j.base.http.HttpHeaders;
import cn.myperf4j.base.http.HttpMethod;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpRespStatus;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.util.collections.ArrayUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static cn.myperf4j.base.http.HttpMethod.POST;
import static cn.myperf4j.base.http.HttpStatusClass.SUCCESS;
import static cn.myperf4j.base.util.collections.ListUtils.isNotEmpty;
import static cn.myperf4j.base.util.io.InputStreamUtils.toBytes;

/**
 * Created by LinShunkang on 2020/05/15
 */
public final class HttpClient {

    private final int connectTimeout;

    private final int readTimeout;

    public HttpClient(Builder builder) {
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
    }

    public HttpResponse execute(HttpRequest request) throws IOException {
        final HttpURLConnection urlConn = createConnection(request);
        urlConn.connect();

        final HttpHeaders headers = new HttpHeaders(urlConn.getHeaderFields());
        final HttpRespStatus status = HttpRespStatus.valueOf(urlConn.getResponseCode());
        if (SUCCESS.contains(status.code())) {
            return new HttpResponse(status, headers, toBytes(urlConn.getInputStream()));
        } else {
            return new HttpResponse(status, headers, toBytes(urlConn.getErrorStream()));
        }
    }

    private HttpURLConnection createConnection(HttpRequest request) throws IOException {
        final URL url = new URL(request.getFullUrl());
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        final HttpMethod method = request.getMethod();
        conn.setRequestMethod(method.getName());
        conn.setDoOutput(method == POST);
        conn.setDoInput(true);
        conn.setUseCaches(false);

        configureHeaders(request, conn);
        writeBody(request, conn);
        return conn;
    }

    private void configureHeaders(HttpRequest request, HttpURLConnection conn) {
        final HttpHeaders headers = request.getHeaders();
        final List<String> names = headers.names();
        for (int i = 0, size = names.size(); i < size; i++) {
            final String name = names.get(i);
            final List<String> values = headers.getValues(name);
            if (isNotEmpty(values)) {
                values.forEach(value -> conn.addRequestProperty(name, value));
            }
        }
    }

    private void writeBody(HttpRequest request, HttpURLConnection conn) throws IOException {
        final HttpMethod method = request.getMethod();
        final byte[] body = request.getBody();
        if (method.isPermitsBody() && ArrayUtils.isNotEmpty(body)) {
            try (BufferedOutputStream bufferedOs = new BufferedOutputStream(conn.getOutputStream())) {
                bufferedOs.write(body);
            }
        }
    }

    public static class Builder {

        private static final int DEFAULT_CONNECT_TIMEOUT = 1000;

        private static final int DEFAULT_READ_TIMEOUT = 3000;

        private int connectTimeout;

        private int readTimeout;

        public Builder() {
            this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
            this.readTimeout = DEFAULT_READ_TIMEOUT;
        }

        public Builder connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }
}
