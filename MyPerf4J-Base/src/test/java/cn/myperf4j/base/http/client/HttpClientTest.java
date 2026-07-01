package cn.myperf4j.base.http.client;

import cn.myperf4j.base.http.HttpHeaders;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.http.server.SimpleHttpServer;
import cn.myperf4j.base.io.Bytes;
import cn.myperf4j.base.util.collections.MapUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.myperf4j.base.http.HttpRespStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpClientTest {

    private static final String RESPONSE_BODY = "Hello!";

    private static final HttpClient httpClient = new HttpClient.Builder().build();

    private static SimpleHttpServer server;

    @BeforeAll
    public static void init() {
        server = new SimpleHttpServer.Builder()
                .port(8686)
                .dispatcher(request -> {
                    final Bytes body = request.getBody();
                    System.out.println("Dispatcher.dispatch(): request.body=" + body.toString(UTF_8));
                    return new HttpResponse(OK, new HttpHeaders(0), RESPONSE_BODY.getBytes(UTF_8));
                })
                .build();
        server.startAsync();
    }

    @AfterAll
    public static void clean() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testGet() {
        final HttpRequest req = new HttpRequest.Builder()
                .url("https://www.baidu.com/sugrec?prod=pc&wd=MyPerf4J&cb=jq")
                .header("Connection", "close")
                .get()
                .build();
        try {
            final HttpResponse resp = httpClient.execute(req);
            final HttpHeaders headers = resp.getHeaders();
            System.out.println("Status=" + resp.getStatus());
            System.out.println("Connection=" + headers.get("Connection"));
            System.out.println(resp.getBodyString());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Test
    public void testPost() {
        final Map<String, List<String>> params = MapUtils.createHashMap(2);
        params.put("db", Collections.singletonList("http"));

        final HttpRequest req = new HttpRequest.Builder()
                .url("localhost:8686/write")
                .params(params)
                .post("cpu_load_short,host=server01,region=us-west value=0.64 1434055562000000000\n" +
                        "cpu_load_short,host=server02,region=us-west value=0.96 1434055562000000000")
                .build();

        for (int i = 0; i < 10; i++) {
            try {
                final HttpResponse resp = httpClient.execute(req);
                Assertions.assertEquals(OK, resp.getStatus());
                Assertions.assertEquals(RESPONSE_BODY, resp.getBodyString());
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
