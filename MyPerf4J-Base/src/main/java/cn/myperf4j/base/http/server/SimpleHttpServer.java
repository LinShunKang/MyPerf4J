package cn.myperf4j.base.http.server;

import cn.myperf4j.base.http.HttpMethod;
import cn.myperf4j.base.http.HttpRequest;
import cn.myperf4j.base.http.HttpResponse;
import cn.myperf4j.base.util.concurrent.ExecutorManager;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.StrUtils;
import com.sun.net.httpserver.Headers;
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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;

import static cn.myperf4j.base.http.HttpMethod.UNKNOWN;
import static cn.myperf4j.base.http.HttpRespStatus.METHOD_NOT_ALLOWED;
import static cn.myperf4j.base.util.io.InputStreamUtils.toBytes;
import static cn.myperf4j.base.util.concurrent.ThreadUtils.newThreadFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Created by LinShunkang on 2020/07/11
 */
public class SimpleHttpServer {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final HttpServer server;

    public SimpleHttpServer(Builder builder) {
        try {
            this.server = HttpServer.create(new InetSocketAddress(builder.port), 0);
            this.server.createContext("/", new DispatchHandler(builder.dispatcher));
            this.server.setExecutor(generateExecutor(builder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Executor generateExecutor(Builder builder) {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(builder.minWorkers,
                builder.maxWorkers,
                1,
                MINUTES,
                new ArrayBlockingQueue<>(builder.acceptCnt),
                newThreadFactory("MyPerf4J-HttpServer-"),
                new DiscardPolicy());
        ExecutorManager.addExecutorService(executor);
        return executor;
    }

    public void startAsync() {
        executor.execute(this::start);
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

        private static final ThreadLocal<StringBuilder> URL_SB = ThreadLocal.withInitial(() -> new StringBuilder(128));

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
            final HttpRequest.Builder reqBuilder = new HttpRequest.Builder()
                    .path(uri.getPath())
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

    public static class Builder {

        private static final int DEFAULT_MIN_WORKERS = 1;

        private static final int DEFAULT_MAX_WORKERS = 2;

        private static final int DEFAULT_ACCEPT_CNT = 1024;

        private int port;

        private int minWorkers;

        private int maxWorkers;

        private int acceptCnt;

        private Dispatcher dispatcher;

        public Builder() {
            this.minWorkers = DEFAULT_MIN_WORKERS;
            this.maxWorkers = DEFAULT_MAX_WORKERS;
            this.acceptCnt = DEFAULT_ACCEPT_CNT;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder minWorkers(int minWorkers) {
            this.minWorkers = minWorkers;
            return this;
        }

        public Builder maxWorkers(int maxWorkers) {
            this.maxWorkers = maxWorkers;
            return this;
        }

        public Builder acceptCnt(int acceptCnt) {
            this.acceptCnt = acceptCnt;
            return this;
        }

        public Builder dispatcher(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
            return this;
        }

        public SimpleHttpServer build() {
            return new SimpleHttpServer(this);
        }
    }
}
