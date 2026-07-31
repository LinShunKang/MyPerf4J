package cn.myperf4j.base.util;

import cn.myperf4j.base.util.net.NetUtils;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by LinShunkang on 2020/12/06
 */
public class NetUtilsTest {

    @Test
    public void test() throws IOException {
        final int port = 1234;
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.start();
        Assertions.assertFalse(NetUtils.isPortAvailable(port));
        Assertions.assertTrue(NetUtils.isPortAvailable(1235));
    }
}
