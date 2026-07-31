package cn.myperf4j.base.influxdb;

import cn.myperf4j.base.io.Bytes;

public interface InfluxDbClient {

    boolean writeMetricsSync(Bytes content);

    boolean writeMetricsAsync(Bytes content);

    boolean close();
}
