package cn.myperf4j.base.influxdb;

public interface InfluxDbClient {

    boolean writeMetricsSync(String content);

    boolean writeMetricsAsync(String content);

    boolean close();
}
