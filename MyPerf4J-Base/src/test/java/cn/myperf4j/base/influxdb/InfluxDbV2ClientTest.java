package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.io.Bytes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by LinShunkang on 2020/05/19
 */
public class InfluxDbV2ClientTest {

    private final InfluxDbV2Client influxDbClient = new InfluxDbV2Client.Builder()
            .orgName("MyOrg")
            .host("127.0.0.1")
            .port(8186)
            .connectTimeout(100)
            .readTimeout(1000)
            .database("MyPerf4J")
            .username("admin")
            .password("admin123")
            .build();

    @Test
    public void testIdentityWrite() {
        Assertions.assertTrue(influxDbClient.writeMetricsSync(
                Bytes.copy("cpu_load_short,host=server01,region=us-west value=0.64 1434055562000000000\n" +
                        "cpu_load_short,host=server02,region=us-west value=0.96 1434055562000000000")));
    }

    @Test
    public void testGzipWrite() {
        final long startMillis = (System.currentTimeMillis() / 1000) * 1000;
        final StringBuilder sb = new StringBuilder(16384);
        for (int i = 0; i < 1024; i++) {
            final long curNanos = (startMillis + i * 1000L) * 1_000_000L;
            sb.append("cpu_load,host=server01,region=china value=3.14 ").append(curNanos).append('\n');
        }

        final boolean write = influxDbClient.writeMetricsSync(Bytes.copy(sb.toString()));
        System.out.println("write = " + write);
    }

    @AfterEach
    public void testClose() {
        Assertions.assertTrue(influxDbClient.close());
    }
}
