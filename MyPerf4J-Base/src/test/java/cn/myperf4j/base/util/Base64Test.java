package cn.myperf4j.base.util;

import cn.myperf4j.base.util.Base64.Encoder;
import org.junit.Assert;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class Base64Test {

    private final Encoder encoder = Encoder.RFC4648;

    @Test
    public void test() {
        byte[] bytes = "Hello, Base64!".getBytes(UTF_8);
        Assert.assertEquals("SGVsbG8sIEJhc2U2NCE=", encoder.encodeToString(bytes));
        Assert.assertArrayEquals("SGVsbG8sIEJhc2U2NCE=".getBytes(UTF_8), encoder.encode(bytes));
    }
}
