package cn.myperf4j.base.http;

import cn.myperf4j.base.util.MapUtils;
import org.junit.Assert;
import org.junit.Test;

import static cn.myperf4j.base.http.HttpMethod.GET;
import static cn.myperf4j.base.http.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpRequestTest {

    @Test
    public void testPostBuilder() {
        HttpRequest req = new HttpRequest.Builder()
                .host("localhost")
                .port(8086)
                .header("Connection", "Keep-Alive")
                .header("Accept-Encoding", "gzip, deflate")
                .path("/write")
                .params(MapUtils.of("k1", "v1"))
                .post("abcd")
                .build();

        Assert.assertEquals("localhost", req.getHost());
        Assert.assertEquals(8086, req.getPort());

        Assert.assertEquals("Keep-Alive", req.getHeaders().get("Connection"));
        Assert.assertEquals("gzip, deflate", req.getHeaders().get("Accept-Encoding"));

        Assert.assertEquals("/write", req.getPath());

        Assert.assertEquals(MapUtils.of("k1", "v1"), req.getParams());

        Assert.assertEquals(POST, req.getMethod());
        Assert.assertArrayEquals("abcd".getBytes(UTF_8), req.getBody());
    }

    @Test
    public void testGetBuilder() {
        HttpRequest req = new HttpRequest.Builder()
                .host("localhost")
                .port(8086)
                .header("Connection", "Keep-Alive")
                .header("Accept-Encoding", "gzip, deflate")
                .path("/write")
                .params(MapUtils.of("k1", "v1"))
                .get()
                .build();

        Assert.assertEquals("localhost", req.getHost());
        Assert.assertEquals(8086, req.getPort());

        Assert.assertEquals("Keep-Alive", req.getHeaders().get("Connection"));
        Assert.assertEquals("gzip, deflate", req.getHeaders().get("Accept-Encoding"));

        Assert.assertEquals("/write", req.getPath());

        Assert.assertEquals(MapUtils.of("k1", "v1"), req.getParams());

        Assert.assertEquals(GET, req.getMethod());
        Assert.assertArrayEquals("".getBytes(UTF_8), req.getBody());
    }
}
