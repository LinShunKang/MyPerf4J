package cn.myperf4j.base.http.server;

import cn.myperf4j.base.http.HttpHeaders;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpRequest.Builder;
import cn.myperf4j.base.http.HttpRespStatus;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.http.client.HttpClient;
import cn.myperf4j.base.util.MapUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/07/12
 */
public class SimpleHttpServerTest {

    private static final HttpClient httpClient = new HttpClient.Builder().build();

    private static final SimpleHttpServer server = new SimpleHttpServer(1024, new Dispatcher() {
        @Override
        public HttpResponse dispatch(HttpRequest request) {
            return new HttpResponse(HttpRespStatus.OK, defaultHeaders(), "Hello, SimpleHttpServer".getBytes(UTF_8));
        }

        private HttpHeaders defaultHeaders() {
            HttpHeaders headers = new HttpHeaders(6);
            headers.set("User-Agent", "MyPerf4J");
            headers.set("Connection", "Keep-Alive");
            headers.set("Charset", UTF_8.name());
            return headers;
        }
    });

    public static void main(String[] args) throws IOException, InterruptedException {
        server.startAsync();
        TimeUnit.SECONDS.sleep(1);

        for (int i = 0; i < 10; i++) {
            final HttpResponse response = httpClient.execute(new Builder()
                    .remoteHost("127.0.0.1")
                    .remotePort(1024)
                    .path("/test")
                    .get()
                    .params(MapUtils.of("k1", Collections.singletonList("v1")))
                    .build());
            System.out.println(response.getBodyString());
        }
    }
}
