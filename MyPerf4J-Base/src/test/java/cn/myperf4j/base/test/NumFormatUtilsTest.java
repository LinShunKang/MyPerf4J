package cn.myperf4j.base.test;

import org.junit.Assert;
import org.junit.Test;

import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;
import static cn.myperf4j.base.util.text.NumFormatUtils.doublePercent;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class NumFormatUtilsTest {

    @Test
    public void testDoubleFormat() {
        Assert.assertEquals("10011.22", doubleFormat(10011.22222D));
        Assert.assertEquals("10011.22", doubleFormat(10011.22D));
        Assert.assertEquals("1.22", doubleFormat(1.2222D));
        Assert.assertEquals("1.20", doubleFormat(1.2D));
        Assert.assertEquals("1.00", doubleFormat(1D));
        Assert.assertEquals("0.00", doubleFormat(0D));
        Assert.assertEquals("-1.00", doubleFormat(-1D));
        Assert.assertEquals("-1.10", doubleFormat(-1.1D));
    }

    @Test
    public void testDoublePercent() {
        Assert.assertEquals("0.00%", doublePercent(0.0D));
        Assert.assertEquals("100.00%", doublePercent(1.0D));
        Assert.assertEquals("-100.00%", doublePercent(-1.0D));

        Assert.assertEquals("20.00%", doublePercent(0.2000D));
        Assert.assertEquals("20.01%", doublePercent(0.2001D));
        Assert.assertEquals("20.00%", doublePercent(0.20D));
        Assert.assertEquals("120.00%", doublePercent(1.20D));
    }

}
