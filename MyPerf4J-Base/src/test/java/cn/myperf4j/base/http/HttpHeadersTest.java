package cn.myperf4j.base.http;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpHeadersTest {

    @Test
    public void testSet() {
        HttpHeaders headers = new HttpHeaders(2);
        headers.set("Connection", "close");
        headers.set("Connection", "Keep-Alive");
        headers.set("Accept-Encoding", "gzip, deflate");

        Assert.assertEquals("Keep-Alive", headers.get("Connection"));
        Assert.assertEquals("gzip, deflate", headers.get("Accept-Encoding"));

        Assert.assertEquals(Collections.singletonList("Keep-Alive"), headers.getValues("Connection"));
        Assert.assertEquals(Collections.singletonList("gzip, deflate"), headers.getValues("Accept-Encoding"));
    }

    @Test
    public void testAdd() {
        HttpHeaders headers = new HttpHeaders(2);
        headers.set("Connection", "Keep-Alive");
        headers.add("Connection", "close");

        Assert.assertEquals("Keep-Alive", headers.get("Connection"));
        Assert.assertEquals(Arrays.asList("Keep-Alive", "close"), headers.getValues("Connection"));
    }

}
