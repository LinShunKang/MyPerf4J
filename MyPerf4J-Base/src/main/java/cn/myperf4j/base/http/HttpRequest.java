package cn.myperf4j.base.http;

import cn.myperf4j.base.util.ArrayUtils;
import cn.myperf4j.base.util.MapUtils;
import cn.myperf4j.base.util.StrUtils;

import java.util.Map;

import static cn.myperf4j.base.http.HttpMethod.GET;
import static cn.myperf4j.base.http.HttpMethod.HEAD;
import static cn.myperf4j.base.http.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/05/15
 */
public final class HttpRequest {

    private static final byte[] EMPTY_BODY = {};

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(512);
        }
    };

    private final String host;

    private final int port;

    private final HttpMethod method;

    private final HttpHeaders headers;

    private final String path;

    private final Map<String, String> params;

    private final byte[] body;

    private String url;

    public HttpRequest(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.method = builder.method;
        this.headers = builder.headers;
        this.path = builder.path;
        this.params = builder.params;
        this.body = builder.body;
        this.url = "";
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public byte[] getBody() {
        return body;
    }

    public String getUrl() {
        if (StrUtils.isNotEmpty(url)) {
            return url;
        }
        return url = createUrl();
    }

    private String createUrl() {
        StringBuilder sb = SB_TL.get();
        try {
            sb.append("http://").append(host).append(':').append(port).append(path);
            if (MapUtils.isEmpty(params)) {
                return sb.toString();
            }

            sb.append('?');
            for (Map.Entry<String, String> param : params.entrySet()) {
                sb.append(param.getKey()).append('=').append(param.getValue()).append('&');
            }
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", path='" + path + '\'' +
                ", params=" + params +
                '}';
    }

    public static class Builder {

        private String host;

        private int port;

        private HttpMethod method;

        private final HttpHeaders headers;

        private String path;

        private Map<String, String> params;

        private byte[] body;

        public Builder() {
            this.method = GET;
            this.headers = HttpHeaders.defaultHeaders();
            this.body = EMPTY_BODY;
        }

        public Builder host(String host) {
            if (StrUtils.isBlank(host)) {
                throw new IllegalArgumentException("host is blank!");
            }
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("port=" + port + " is invalid!");
            }
            this.port = port;
            return this;
        }


        public Builder path(String path) {
            if (StrUtils.isBlank(path)) {
                throw new IllegalArgumentException("path is blank!");
            }
            this.path = path;
            return this;
        }

        public Builder params(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder header(String name, String value) {
            headers.set(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public Builder head() {
            return method(HEAD, EMPTY_BODY);
        }

        public Builder get() {
            return method(GET, EMPTY_BODY);
        }

        public Builder post(byte[] body) {
            return method(POST, body);
        }

        public Builder post(String body) {
            return method(POST, body.getBytes(UTF_8));
        }

        public Builder method(HttpMethod method, byte[] body) {
            if (method == null) {
                throw new IllegalArgumentException("method is null!");
            }

            if (ArrayUtils.isNotEmpty(body) && !method.isPermitsBody()) {
                throw new IllegalArgumentException("method " + method + " must not have a request body!");
            }

            if (ArrayUtils.isEmpty(body) && method.isPermitsBody()) {
                throw new IllegalArgumentException("method " + method + " must have a request body!");
            }

            this.method = method;
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            if (path == null) {
                throw new IllegalStateException("path is null");
            }
            return new HttpRequest(this);
        }
    }
}
