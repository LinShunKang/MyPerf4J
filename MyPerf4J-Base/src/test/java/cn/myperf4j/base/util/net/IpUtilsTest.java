package cn.myperf4j.base.util.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by LinShunkang on 2022/02/04
 */
public class IpUtilsTest {

    @Test
    public void testGetLocalhostName() {
        Assertions.assertNotEquals("127.0.0.1", IpUtils.getLocalhostName());
        Assertions.assertNotEquals("localhost", IpUtils.getLocalhostName());
    }

    @Test
    public void testGetLocalIp() {
        Assertions.assertNotEquals("127.0.0.1", IpUtils.getLocalIp());
        Assertions.assertNotEquals("localhost", IpUtils.getLocalIp());
    }
}
