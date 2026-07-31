package cn.myperf4j.base.http;

import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.util.StrUtils;
import cn.myperf4j.base.util.collections.MapUtils;

import java.util.List;
import java.util.Map;

import static cn.myperf4j.base.http.HttpMethod.GET;
import static cn.myperf4j.base.http.HttpMethod.HEAD;
import static cn.myperf4j.base.http.HttpMethod.POST;
import static cn.myperf4j.base.io.Bytes.unsafeWrap;
import static cn.myperf4j.base.util.StrUtils.isNotEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/05/15
 */
public final class HttpRequest {

    private static final Bytes EMPTY_BODY = unsafeWrap(new byte[0]);

    private static final ThreadLocal<StringBuilder> SB_TL = ThreadLocal.withInitial(() -> new StringBuilder(512));

    private final String path;

    private final String url;

    private final HttpMethod method;

    private final HttpHeaders headers;

    private final Map<String, List<String>> params;

    private final Bytes body;

    private String fullUrl;

    public HttpRequest(Builder builder) {
        this.path = builder.path;
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.params = builder.params;
        this.body = builder.body;
        this.fullUrl = "";
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Map<String, List<String>> getParams() {
        return params;
    }

    public Bytes getBody() {
        return body;
    }

    public String getPath() {
        return path;
    }

    public String getFullUrl() {
        return isNotEmpty(fullUrl) ? fullUrl : (fullUrl = createFullUrl());
    }

    private String createFullUrl() {
        final StringBuilder sb = SB_TL.get();
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                sb.append("http://");
            }

            sb.append(url);
            if (MapUtils.isEmpty(params)) {
                return sb.toString();
            }

            if (!StrUtils.isContains(url, '?')) {
                sb.append('?');
            } else if (!StrUtils.isEndWith(url, '?')) {
                sb.append('&');
            }

            params.forEach((k, vs) -> vs.forEach(v -> sb.append(k).append('=').append(v).append('&')));
            return sb.substring(0, sb.length() - 1);
        } finally {
            sb.setLength(0);
        }
    }

    public String getParam(String key) {
        final List<String> values = params.get(key);
        return values != null ? values.get(0) : null;
    }

    public Boolean getBoolParam(String key) {
        final String value = getParam(key);
        return Boolean.valueOf(value);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", headers=" + headers +
                ", params=" + params +
                '}';
    }

    public static class Builder {

        private String path;

        private String url;

        private HttpMethod method;

        private HttpHeaders headers;

        private Map<String, List<String>> params;

        private Bytes body;

        public Builder() {
            this.method = GET;
            this.headers = HttpHeaders.defaultHeaders();
            this.body = EMPTY_BODY;
        }

        public Builder path(String path) {
            if (StrUtils.isBlank(path)) {
                throw new IllegalArgumentException("path is blank!");
            }
            this.path = path;
            return this;
        }

        public Builder url(String url) {
            if (StrUtils.isBlank(url)) {
                throw new IllegalArgumentException("url is blank!");
            }
            this.url = url;
            return this;
        }

        public Builder params(Map<String, List<String>> params) {
            this.params = params;
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            this.headers = new HttpHeaders(headers);
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        public Builder head() {
            return method(HEAD, EMPTY_BODY);
        }

        public Builder get() {
            return method(GET, EMPTY_BODY);
        }

        public Builder post(String body) {
            return post(body.getBytes(UTF_8));
        }

        public Builder post(byte[] body) {
            return method(POST, unsafeWrap(body));
        }

        public Builder post(Bytes body) {
            return method(POST, body);
        }

        public Builder method(HttpMethod method, Bytes body) {
            if (method == null || body == null) {
                throw new IllegalArgumentException("method or body is null!");
            }

            if (!method.isPermitsBody() && body.isNotEmpty()) {
                throw new IllegalArgumentException("method " + method + " must not have a request body!");
            }

            if (method.isPermitsBody() && body.isEmpty()) {
                throw new IllegalArgumentException("method " + method + " must have a request body!");
            }

            this.method = method;
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            if (url == null) {
                throw new IllegalStateException("url is null!");
            }
            return new HttpRequest(this);
        }
    }
}
