package cn.myperf4j.base.http;

import cn.myperf4j.base.util.InputStreamUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LinShunkang on 2020/05/15
 */
public final class HttpResponse implements Closeable {

    private final HttpRespStatus status;

    private final HttpHeaders headers;

    private final InputStream inputStream;

    public HttpResponse(HttpRespStatus status, HttpHeaders headers, InputStream inputStream) {
        this.status = status;
        this.headers = headers;
        this.inputStream = inputStream;
    }

    public HttpRespStatus getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBodyString() throws IOException {
        return InputStreamUtils.toString(inputStream);
    }

    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "status=" + status +
                ", body='" + inputStream + '\'' +
                '}';
    }
}
