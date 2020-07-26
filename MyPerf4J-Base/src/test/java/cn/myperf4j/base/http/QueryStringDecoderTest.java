package cn.myperf4j.base.http;

import cn.myperf4j.base.http.server.QueryStringDecoder;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created by LinShunkang on 2020/07/12
 */
public class QueryStringDecoderTest {

    @Test
    public void testHasPath() {
        final String encodedURL = "/api/test?k1=v1&k2=v2&k3=v3";
        QueryStringDecoder appDecoder = new QueryStringDecoder(encodedURL, StandardCharsets.UTF_8, true);
        Map<String, List<String>> params = appDecoder.parameters();
        System.out.println(params);
    }

    @Test
    public void testNoPath() {
        final String encodedURL = "k1=v1&k2=v2&k3=v3";
        QueryStringDecoder appDecoder = new QueryStringDecoder(encodedURL, StandardCharsets.UTF_8, false);
        Map<String, List<String>> params = appDecoder.parameters();
        System.out.println(params);
    }
}
