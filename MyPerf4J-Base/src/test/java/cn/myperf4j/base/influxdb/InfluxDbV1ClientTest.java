package cn.myperf4j.base.influxdb;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2020/05/19
 */
public class InfluxDbV1ClientTest {

    private final InfluxDbV1Client influxDbV1Client = new InfluxDbV1Client.Builder()
            .host("127.0.0.1")
            .port(8086)
            .connectTimeout(100)
            .readTimeout(1000)
            .database("test_db_0")
            .username("admin")
            .password("admin123")
            .build();

    @Test
    public void testWrite() throws InterruptedException {
        boolean write = influxDbV1Client.writeMetricsAsync(
                "cpu_load_short,host=server01,region=us-west value=0.64 1434055562000000000\n" +
                        "cpu_load_short,host=server02,region=us-west value=0.96 1434055562000000000");
        System.out.println(write);
        TimeUnit.SECONDS.sleep(3);
    }
}
