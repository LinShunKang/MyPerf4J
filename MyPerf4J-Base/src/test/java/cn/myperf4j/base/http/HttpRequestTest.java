package cn.myperf4j.base.http;

import cn.myperf4j.base.util.collections.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.http.HttpMethod.GET;
import static cn.myperf4j.base.http.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpRequestTest {

    @Test
    public void testPostBuilder() {
        HttpRequest req = new HttpRequest.Builder()
                .url("localhost:8086/write?k1=v1")
                .header("Connection", "Keep-Alive")
                .header("Accept-Encoding", "gzip, deflate")
                .params(MapUtils.of("k2", singletonList("v2")))
                .post("abcd")
                .build();

        Assertions.assertEquals("http://localhost:8086/write?k1=v1&k2=v2", req.getFullUrl());

        Assertions.assertEquals("Keep-Alive", req.getHeaders().get("Connection"));
        Assertions.assertEquals("gzip, deflate", req.getHeaders().get("Accept-Encoding"));

        Assertions.assertEquals(MapUtils.of("k2", singletonList("v2")), req.getParams());

        Assertions.assertEquals(POST, req.getMethod());
        Assertions.assertArrayEquals("abcd".getBytes(UTF_8), req.getBody());
    }

    @Test
    public void testGetBuilder() {
        HttpRequest req = new HttpRequest.Builder()
                .url("localhost:8086/write?k1=v1")
                .header("Connection", "Keep-Alive")
                .header("Accept-Encoding", "gzip, deflate")
                .params(MapUtils.of("k2", singletonList("v2")))
                .get()
                .build();

        Assertions.assertEquals("http://localhost:8086/write?k1=v1&k2=v2", req.getFullUrl());

        Assertions.assertEquals("Keep-Alive", req.getHeaders().get("Connection"));
        Assertions.assertEquals("gzip, deflate", req.getHeaders().get("Accept-Encoding"));

        Assertions.assertEquals(MapUtils.of("k2", singletonList("v2")), req.getParams());

        Assertions.assertEquals(GET, req.getMethod());
        Assertions.assertArrayEquals("".getBytes(UTF_8), req.getBody());
    }

    @Test
    public void testFullUrlWithoutProtocol() {
        HttpRequest req0 = new HttpRequest.Builder()
                .url("localhost:8086/write")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assertions.assertEquals("http://localhost:8086/write?k1=v1", req0.getFullUrl());

        HttpRequest req1 = new HttpRequest.Builder()
                .url("localhost:8086/write?")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assertions.assertEquals("http://localhost:8086/write?k1=v1", req1.getFullUrl());

        HttpRequest req2 = new HttpRequest.Builder()
                .url("localhost:8086/write?k1=v1")
                .params(MapUtils.of("k2", singletonList("v2")))
                .post("abcd")
                .build();
        Assertions.assertEquals("http://localhost:8086/write?k1=v1&k2=v2", req2.getFullUrl());
    }

    @Test
    public void testFullUrlWithProtocol() {
        HttpRequest req0 = new HttpRequest.Builder()
                .url("http://localhost:8086/write")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assertions.assertEquals("http://localhost:8086/write?k1=v1", req0.getFullUrl());

        HttpRequest req1 = new HttpRequest.Builder()
                .url("http://localhost:8086/write?")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assertions.assertEquals("http://localhost:8086/write?k1=v1", req1.getFullUrl());

        HttpRequest req2 = new HttpRequest.Builder()
                .url("https://localhost:8086/write?k1=v1")
                .params(MapUtils.of("k2", singletonList("v2")))
                .post("abcd")
                .build();
        Assertions.assertEquals("https://localhost:8086/write?k1=v1&k2=v2", req2.getFullUrl());
    }
}
