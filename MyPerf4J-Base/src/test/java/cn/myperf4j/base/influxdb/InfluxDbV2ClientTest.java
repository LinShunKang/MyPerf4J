package cn.myperf4j.base.influxdb;

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
    public void testWrite() {
        Assertions.assertTrue(influxDbClient.writeMetricsSync(
                "cpu_load_short,host=server01,region=us-west value=0.64 1434055562000000000\n" +
                        "cpu_load_short,host=server02,region=us-west value=0.96 1434055562000000000"));
    }

    @AfterEach
    public void testClose() {
        Assertions.assertTrue(influxDbClient.close());
    }
}
