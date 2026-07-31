package cn.myperf4j.base.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        Assertions.assertEquals("Keep-Alive", headers.get("Connection"));
        Assertions.assertEquals("gzip, deflate", headers.get("Accept-Encoding"));

        Assertions.assertEquals(Collections.singletonList("Keep-Alive"), headers.getValues("Connection"));
        Assertions.assertEquals(Collections.singletonList("gzip, deflate"), headers.getValues("Accept-Encoding"));
    }

    @Test
    public void testAdd() {
        HttpHeaders headers = new HttpHeaders(2);
        headers.set("Connection", "Keep-Alive");
        headers.add("Connection", "close");

        Assertions.assertEquals("Keep-Alive", headers.get("Connection"));
        Assertions.assertEquals(Arrays.asList("Keep-Alive", "close"), headers.getValues("Connection"));
    }

}
