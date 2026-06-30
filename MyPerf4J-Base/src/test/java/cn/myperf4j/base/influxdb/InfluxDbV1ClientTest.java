package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.io.Bytes;
import org.junit.Test;

/**
 * Created by LinShunkang on 2020/05/19
 */
public class InfluxDbV1ClientTest {

    private final InfluxDbV1Client influxDbV1Client = new InfluxDbV1Client.Builder()
            .host("127.0.0.1")
            .port(8086)
            .connectTimeout(100)
            .readTimeout(1000)
            .database("MyPerf4J")
            .username("admin")
            .password("admin123")
            .build();

    @Test
    public void testIdentityWrite() {
        final boolean write = influxDbV1Client.writeMetricsSync(
                Bytes.copy("cpu_load_short,host=server01,region=us-west value=0.68 1782023598000000000\n" +
                        "cpu_load_short,host=server02,region=us-west value=0.98 1782023598000000000"));
        System.out.println("write = " + write);
    }

    @Test
    public void testGzipWrite() {
        final long startMillis = (System.currentTimeMillis() / 1000) * 1000;
        final StringBuilder sb = new StringBuilder(16384);
        for (int i = 0; i < 1024; i++) {
            final long curNanos = (startMillis + i * 1000L) * 1_000_000L;
            sb.append("cpu_load,host=server01,region=china value=3.14 ").append(curNanos).append('\n');
        }

        final boolean write = influxDbV1Client.writeMetricsSync(Bytes.copy(sb.toString()));
        System.out.println("write = " + write);
    }
}
