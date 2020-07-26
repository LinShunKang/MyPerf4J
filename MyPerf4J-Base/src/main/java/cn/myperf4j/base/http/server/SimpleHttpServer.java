package cn.myperf4j.base.http.server;

import cn.myperf4j.base.http.HttpMethod;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpRequest.Builder;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.StrUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.myperf4j.base.http.HttpMethod.UNKNOWN;
import static cn.myperf4j.base.http.HttpRespStatus.METHOD_NOT_ALLOWED;
import static cn.myperf4j.base.util.InputStreamUtils.toBytes;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/07/11
 */
public class SimpleHttpServer {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final HttpServer server;

    public SimpleHttpServer(int port, Dispatcher dispatcher) {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
            this.server.createContext("/", new DispatchHandler(dispatcher));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startAsync() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    /**
     * 启动 HTTP 服务器，启动后会阻塞当前线程
     */
    public void start() {
        final InetSocketAddress address = server.getAddress();
        Logger.info("SimpleHttpServer listen on " + address.getHostName() + ":" + address.getPort());
        server.start();
    }

    public void stop() {
        server.stop(3);
    }

    private static class DispatchHandler implements HttpHandler {

        private static final ThreadLocal<StringBuilder> URL_SB = new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                return new StringBuilder(128);
            }
        };

        private final Dispatcher dispatcher;

        DispatchHandler(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            final HttpMethod httpMethod = HttpMethod.parse(exchange.getRequestMethod());
            if (httpMethod == UNKNOWN) {
                exchange.sendResponseHeaders(METHOD_NOT_ALLOWED.code(), 0);
                OutputStream os = exchange.getResponseBody();
                os.write("".getBytes(UTF_8));
                os.close();
                return;
            }

            try {
                final HttpRequest httpRequest = parseHttpRequest(exchange, httpMethod);
                final HttpResponse response = dispatcher.dispatch(httpRequest);
                final OutputStream responseBody = exchange.getResponseBody();
                final Headers respHeaders = exchange.getResponseHeaders();
                respHeaders.putAll(response.getHeaders().headers());

                exchange.sendResponseHeaders(response.getStatus().code(), 0);
                responseBody.write(response.getBody());
                responseBody.flush();
            } catch (Exception e) {
                Logger.error("DispatchHandler.handle()", e);
            } finally {
                exchange.close();
            }
        }

        private HttpRequest parseHttpRequest(HttpExchange exchange, HttpMethod httpMethod) throws IOException {
            final URI uri = exchange.getRequestURI();
            final Builder reqBuilder = new Builder()
                    .url(buildUrl(exchange))
                    .headers(exchange.getRequestHeaders())
                    .params(parseParams(uri.getRawQuery()))
                    .method(httpMethod, toBytes(exchange.getRequestBody()));
            return reqBuilder.build();
        }

        private String buildUrl(HttpExchange exchange) {
            final StringBuilder sb = URL_SB.get();
            try {
                final InetSocketAddress localAddr = exchange.getLocalAddress();
                final URI uri = exchange.getRequestURI();
                sb.append("http://")
                        .append(localAddr.getHostString()).append(':').append(localAddr.getPort())
                        .append(uri.getPath()).append('?')
                        .append(uri.getRawQuery());
                return sb.toString();
            } finally {
                sb.setLength(0);
            }
        }

        private Map<String, List<String>> parseParams(String rawQuery) {
            if (StrUtils.isBlank(rawQuery)) {
                return Collections.emptyMap();
            }
            return new QueryStringDecoder(rawQuery, UTF_8, false).parameters();
        }
    }
}
