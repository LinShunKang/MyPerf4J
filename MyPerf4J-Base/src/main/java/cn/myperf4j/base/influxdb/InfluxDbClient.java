package cn.myperf4j.base.influxdb;

public interface InfluxDbClient {

    boolean createDatabase();

    boolean writeMetricsAsync(String content);
}
