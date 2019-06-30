package cn.myperf4j.base.test;

import org.junit.Assert;
import org.junit.Test;

import static cn.myperf4j.base.util.NumFormatUtils.formatDouble;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class NumFormatUtilsTest {

    @Test
    public void test() {
        Assert.assertEquals("10011.22", formatDouble(10011.22222D));
        Assert.assertEquals("10011.22", formatDouble(10011.22D));
        Assert.assertEquals("1.22", formatDouble(1.2222D));
        Assert.assertEquals("1.20", formatDouble(1.2D));
        Assert.assertEquals("1.00", formatDouble(1D));
        Assert.assertEquals("0.00", formatDouble(0D));
        Assert.assertEquals("-1.00", formatDouble(-1D));
        Assert.assertEquals("-1.10", formatDouble(-1.1D));
    }
}
