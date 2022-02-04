package cn.myperf4j.base.influxdb;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2020/05/19
 */
public class InfluxDbV2ClientTest {

    private final InfluxDbV2Client influxDbClient = new InfluxDbV2Client.Builder()
            .orgName("localhost")
            .host("127.0.0.1")
            .port(8086)
            .connectTimeout(100)
            .readTimeout(1000)
            .database("MyPerf4J")
            .username("admin")
            .password("admin123")
            .build();

    @Test
    public void testWrite() {
        Assert.assertTrue(influxDbClient.writeMetricsSync(
                "cpu_load_short,host=server01,region=us-west value=0.64 1434055562000000000\n" +
                        "cpu_load_short,host=server02,region=us-west value=0.96 1434055562000000000"));
    }

    @After
    public void testClose() {
        Assert.assertTrue(influxDbClient.close());
    }
}
