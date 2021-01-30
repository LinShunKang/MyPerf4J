package cn.myperf4j.base.http;

import cn.myperf4j.base.util.collections.MapUtils;
import org.junit.Assert;
import org.junit.Test;

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

        Assert.assertEquals("http://localhost:8086/write?k1=v1&k2=v2", req.getFullUrl());

        Assert.assertEquals("Keep-Alive", req.getHeaders().get("Connection"));
        Assert.assertEquals("gzip, deflate", req.getHeaders().get("Accept-Encoding"));

        Assert.assertEquals(MapUtils.of("k2", singletonList("v2")), req.getParams());

        Assert.assertEquals(POST, req.getMethod());
        Assert.assertArrayEquals("abcd".getBytes(UTF_8), req.getBody());
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

        Assert.assertEquals("http://localhost:8086/write?k1=v1&k2=v2", req.getFullUrl());

        Assert.assertEquals("Keep-Alive", req.getHeaders().get("Connection"));
        Assert.assertEquals("gzip, deflate", req.getHeaders().get("Accept-Encoding"));

        Assert.assertEquals(MapUtils.of("k2", singletonList("v2")), req.getParams());

        Assert.assertEquals(GET, req.getMethod());
        Assert.assertArrayEquals("".getBytes(UTF_8), req.getBody());
    }

    @Test
    public void testFullUrlWithoutProtocol() {
        HttpRequest req0 = new HttpRequest.Builder()
                .url("localhost:8086/write")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assert.assertEquals("http://localhost:8086/write?k1=v1", req0.getFullUrl());

        HttpRequest req1 = new HttpRequest.Builder()
                .url("localhost:8086/write?")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assert.assertEquals("http://localhost:8086/write?k1=v1", req1.getFullUrl());

        HttpRequest req2 = new HttpRequest.Builder()
                .url("localhost:8086/write?k1=v1")
                .params(MapUtils.of("k2", singletonList("v2")))
                .post("abcd")
                .build();
        Assert.assertEquals("http://localhost:8086/write?k1=v1&k2=v2", req2.getFullUrl());
    }

    @Test
    public void testFullUrlWithProtocol() {
        HttpRequest req0 = new HttpRequest.Builder()
                .url("http://localhost:8086/write")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assert.assertEquals("http://localhost:8086/write?k1=v1", req0.getFullUrl());

        HttpRequest req1 = new HttpRequest.Builder()
                .url("http://localhost:8086/write?")
                .params(MapUtils.of("k1", singletonList("v1")))
                .get()
                .build();
        Assert.assertEquals("http://localhost:8086/write?k1=v1", req1.getFullUrl());

        HttpRequest req2 = new HttpRequest.Builder()
                .url("https://localhost:8086/write?k1=v1")
                .params(MapUtils.of("k2", singletonList("v2")))
                .post("abcd")
                .build();
        Assert.assertEquals("https://localhost:8086/write?k1=v1&k2=v2", req2.getFullUrl());
    }
}
