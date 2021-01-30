package cn.myperf4j.base.http.client;

import cn.myperf4j.base.http.HttpHeaders;
import cn.myperf4j.base.http.HttpMethod;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpRespStatus;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.util.collections.ArrayUtils;
import cn.myperf4j.base.util.collections.ListUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static cn.myperf4j.base.http.HttpMethod.POST;
import static cn.myperf4j.base.http.HttpStatusClass.SUCCESS;
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
        HttpURLConnection urlConn = createConnection(request);
        urlConn.connect();

        HttpHeaders headers = new HttpHeaders(urlConn.getHeaderFields());
        HttpRespStatus status = HttpRespStatus.valueOf(urlConn.getResponseCode());
        if (SUCCESS.contains(status.code())) {
            return new HttpResponse(status, headers, toBytes(urlConn.getInputStream()));
        } else {
            return new HttpResponse(status, headers, toBytes(urlConn.getErrorStream()));
        }
    }

    private HttpURLConnection createConnection(HttpRequest request) throws IOException {
        URL url = new URL(request.getFullUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        HttpMethod method = request.getMethod();
        conn.setRequestMethod(method.getName());
        conn.setDoOutput(method == POST);
        conn.setDoInput(true);
        conn.setUseCaches(false);

        configureHeaders(request, conn);
        writeBody(request, conn);
        return conn;
    }

    private void configureHeaders(HttpRequest request, HttpURLConnection conn) {
        HttpHeaders headers = request.getHeaders();
        List<String> names = headers.names();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            List<String> values = headers.getValues(name);
            if (ListUtils.isEmpty(values)) {
                continue;
            }

            for (int k = 0; k < values.size(); k++) {
                conn.addRequestProperty(name, values.get(k));
            }
        }
    }

    private void writeBody(HttpRequest request, HttpURLConnection conn) throws IOException {
        HttpMethod method = request.getMethod();
        byte[] body = request.getBody();
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
