package cn.myperf4j.base.util.net;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2022/02/04
 */
public class IpUtilsTest {

    @Test
    public void testGetLocalhostName() {
        Assert.assertNotEquals("127.0.0.1", IpUtils.getLocalhostName());
        Assert.assertNotEquals("localhost", IpUtils.getLocalhostName());
    }

    @Test
    public void testGetLocalIp() {
        Assert.assertNotEquals("127.0.0.1", IpUtils.getLocalIp());
        Assert.assertNotEquals("localhost", IpUtils.getLocalIp());
    }
}
