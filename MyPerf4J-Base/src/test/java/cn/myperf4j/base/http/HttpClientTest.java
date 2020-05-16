package cn.myperf4j.base.http;

import cn.myperf4j.base.util.MapUtils;
import org.junit.Test;

import java.util.Map;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpClientTest {

    private final HttpClient httpClient = new HttpClient(1000, 2000);

    @Test
    public void testGet() {
        HttpRequest req = new HttpRequest.Builder()
                .host("www.baidu.com")
                .port(80)
                .path("/")
//                .header("Connection", "close")
                .get()
                .build();
        try (HttpResponse resp = httpClient.execute(req)) {
            HttpHeaders headers = resp.getHeaders();
            System.out.println("Status=" + resp.getStatus());
            System.out.println("Connection=" + headers.get("Connection"));
            System.out.println(resp.getBodyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPost() {
        Map<String, String> params = MapUtils.createHashMap(2);
        params.put("db", "http");

        HttpRequest req = new HttpRequest.Builder()
                .host("localhost")
                .port(8086)
                .path("/write")
                .params(params)
                .post("cpu_load_short,host=server01,region=us-west value=0.64 1434055562000000000")
                .build();
        try (HttpResponse resp = httpClient.execute(req)) {
            HttpHeaders headers = resp.getHeaders();
            System.out.println("Status=" + resp.getStatus());
            System.out.println("Connection=" + headers.get("Connection"));
            System.out.println(resp.getBodyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
