package cn.myperf4j.base.http.server;

import cn.myperf4j.base.util.io.InputStreamUtils;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2020/07/11
 */
public class HttpServerTest {

    /**
     * curl 'localhost:1024/start?k1=v1&k2=v2&k3=v3' -X POST -d '{"param":"value"}'
     */
    @Test
    public void test() throws InterruptedException, IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(1024), 0);
        server.createContext("/start", new StartHandler());
        server.createContext("/", new TestHandler("/"));
        server.createContext("/test", new TestHandler("/test"));
        server.createContext("/test/lv2", new TestHandler("/test/lv2"));
        server.createContext("/test/lv2/lv3", new TestHandler("/test/lv2/lv3"));
        server.start();

        TimeUnit.SECONDS.sleep(1);

        server.stop(1);
    }

    private static class StartHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("getProtocol:" + exchange.getProtocol()); //协议版本
            System.out.println("getRequestMethod:" + exchange.getRequestMethod()); //请求方法GET、POST
            System.out.println("getResponseCode:" + exchange.getResponseCode()); //返回已经设置的响应code，还没设置返回-1

            HttpContext context = exchange.getHttpContext();
            System.out.println("context.getPath:" + context.getPath());
            System.out.println("context.toString:" + context.toString());
            System.out.println("context.getAttributes：" + context.getAttributes());
            System.out.println("context.getAuthenticator：" + context.getAuthenticator());
            System.out.println("context.getFilters：" + context.getFilters());
            System.out.println("context.getHandler：" + context.getHandler());
            System.out.println("context.getServer：" + context.getServer());

            System.out.println("getLocalAddress:" + exchange.getLocalAddress());
            System.out.println("getPrincipal:" + exchange.getPrincipal());
            System.out.println("getRemoteAddress:" + exchange.getRemoteAddress());

            URI uri = exchange.getRequestURI();
            System.out.println("getRequestURI:" + exchange.getRequestURI());
            System.out.println("uri.getAuthority:" + uri.getAuthority());
            System.out.println("uri.getFragment:" + uri.getFragment());
            System.out.println("uri.getHost:" + uri.getHost());
            System.out.println("uri.getPath:" + uri.getPath());
            System.out.println("uri.getQuery:" + uri.getQuery()); //url里get请求的参数
            System.out.println("uri.getRawAuthority:" + uri.getRawAuthority());
            System.out.println("uri.getRawFragment:" + uri.getRawFragment());
            System.out.println("uri.getRawPath:" + uri.getRawPath());
            System.out.println("uri.getRawQuery:" + uri.getRawQuery());
            System.out.println("uri.getRawSchemeSpecificPart:" + uri.getRawSchemeSpecificPart());
            System.out.println("uri.getRawUserInfo:" + uri.getRawUserInfo());
            System.out.println("uri.getScheme:" + uri.getScheme());
            System.out.println("uri.getSchemeSpecificPart:" + uri.getSchemeSpecificPart());
            System.out.println("uri.getUserInfo:" + uri.getUserInfo());
            System.out.println("uri.getPort:" + uri.getPort());

            System.out.println("getRequestBody:" + InputStreamUtils.toString(exchange.getRequestBody()));
            try (OutputStream responseBody = exchange.getResponseBody()) {
                System.out.println("getResponseBody:" + responseBody);

                //len > 0：响应体必须发送指定长度；
                //len = 0：可发送任意长度，关闭 OutputStream 即可停止；
                //len = -1：不会发响应体；
                exchange.sendResponseHeaders(200, 0);
                responseBody.write("Hello, HttpServer!".getBytes(StandardCharsets.UTF_8));
                responseBody.flush();
            }

            System.out.println("getResponseHeaders:" + exchange.getResponseHeaders());
            System.out.println("ResponseCode:" + exchange.getResponseCode());
            exchange.close(); //先关闭打开的InputStream，再关闭打开的OutputStream
        }
    }

    static class TestHandler implements HttpHandler {

        private final String name;

        TestHandler(String name) {
            this.name = name;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello, I'm " + name;
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}
