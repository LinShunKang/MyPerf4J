package cn.myperf4j.base.http;

import cn.myperf4j.base.util.ListUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static cn.myperf4j.base.http.HttpMethod.POST;

/**
 * Created by LinShunkang on 2020/05/15
 */
public final class HttpClient {

    private final int connectTimeout;

    private final int readTimeout;

    public HttpClient(int connectTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public HttpResponse execute(HttpRequest request) throws IOException {
        HttpURLConnection urlConn = createConnection(request);
        urlConn.connect();

        HttpHeaders headers = new HttpHeaders(urlConn.getHeaderFields());
        HttpRespStatus status = HttpRespStatus.valueOf(urlConn.getResponseCode());
        if (HttpStatusClass.SUCCESS.contains(status.getCode())) {
            return new HttpResponse(status, headers, urlConn.getInputStream());
        } else {
            return new HttpResponse(status, headers, urlConn.getErrorStream());
        }
    }

    private HttpURLConnection createConnection(HttpRequest request) throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        HttpMethod method = request.getMethod();
        conn.setRequestMethod(method.getName());
        conn.setDoOutput(method == POST);
        conn.setDoInput(true);
        conn.setUseCaches(false);

        configureHeaders(request, conn);

        //        conn.setRequestProperty("Connection", "Keep-Alive");
//        conn.setRequestProperty("Charset", UTF_8.name());
//        conn.setRequestProperty("User-Agent", "MyPerf4J");
//        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

        writeBody(request, conn);
        return conn;
    }

    private void configureHeaders(HttpRequest request, HttpURLConnection conn) {
        HttpHeaders headers = request.getHeaders();
        List<String> headerNames = headers.names();
        for (int i = 0; i < headerNames.size(); i++) {
            String name = headerNames.get(i);
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
        if (method.isPermitsBody() && body != null && body.length > 0) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(conn.getOutputStream())) {
                bufferedOutputStream.write(body);
            }
        }
    }

}
