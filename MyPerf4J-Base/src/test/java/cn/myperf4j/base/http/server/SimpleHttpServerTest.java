package cn.myperf4j.base.http.server;

import cn.myperf4j.base.http.HttpHeaders;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpRequest.Builder;
import cn.myperf4j.base.http.HttpRespStatus;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.http.client.HttpClient;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.collections.MapUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/07/12
 */
public class SimpleHttpServerTest {

    private static final String RESP_STR = "Hello, SimpleHttpServer";

    private static final int PORT = 1024;

    private static final HttpClient httpClient = new HttpClient.Builder().connectTimeout(3000).build();

    private static final SimpleHttpServer server = new SimpleHttpServer.Builder()
            .port(PORT)
            .minWorkers(8)
            .maxWorkers(64)
            .acceptCnt(1024)
            .dispatcher(new Dispatcher() {
                @Override
                public HttpResponse dispatch(HttpRequest request) {
                    Logger.info(" dispatching...");
                    return new HttpResponse(HttpRespStatus.OK, defaultHeaders(), RESP_STR.getBytes(UTF_8));
                }

                private HttpHeaders defaultHeaders() {
                    HttpHeaders headers = new HttpHeaders(6);
                    headers.set("User-Agent", "MyPerf4J");
                    headers.set("Connection", "Keep-Alive");
                    headers.set("Charset", UTF_8.name());
                    return headers;
                }
            })
            .build();

    @BeforeClass
    public static void start() {
        server.startAsync();
    }

    @AfterClass
    public static void stop() {
        server.stop();
    }

    @Test
    public void test() throws InterruptedException {
        final int TEST_TIMES = 1000;
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        final CountDownLatch latch = new CountDownLatch(TEST_TIMES);
        for (int i = 0; i < TEST_TIMES; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        long startMillis = System.currentTimeMillis();
                        final HttpResponse response = httpClient.execute(new Builder()
                                .url("127.0.0.1:" + PORT + "/test")
                                .get()
                                .params(MapUtils.of("k1", Collections.singletonList("v1")))
                                .build());
                        Logger.info(" Receive response=" + response.getBodyString() +
                                ", cost=" + (System.currentTimeMillis() - startMillis) + "ms");
                        Assert.assertEquals(RESP_STR, response.getBodyString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        latch.await();
        executor.shutdownNow();
    }
}
