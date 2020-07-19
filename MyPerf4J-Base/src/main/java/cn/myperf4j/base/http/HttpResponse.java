package cn.myperf4j.base.http;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/05/15
 */
public final class HttpResponse {

    private final HttpRespStatus status;

    private final HttpHeaders headers;

    private final byte[] body;

    public HttpResponse(HttpRespStatus status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public HttpRespStatus getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyString() {
        return new String(body, 0, body.length, UTF_8);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "status=" + status +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
