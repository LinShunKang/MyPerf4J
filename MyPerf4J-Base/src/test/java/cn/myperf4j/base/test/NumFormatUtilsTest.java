package cn.myperf4j.base.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.util.text.NumFormatUtils.doubleFormat;
import static cn.myperf4j.base.util.text.NumFormatUtils.doublePercent;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class NumFormatUtilsTest {

    @Test
    public void testDoubleFormat() {
        Assertions.assertEquals("10011.22", doubleFormat(10011.22222D));
        Assertions.assertEquals("10011.22", doubleFormat(10011.22D));
        Assertions.assertEquals("1.22", doubleFormat(1.2222D));
        Assertions.assertEquals("1.20", doubleFormat(1.2D));
        Assertions.assertEquals("1.00", doubleFormat(1D));
        Assertions.assertEquals("0.00", doubleFormat(0D));
        Assertions.assertEquals("-1.00", doubleFormat(-1D));
        Assertions.assertEquals("-1.10", doubleFormat(-1.1D));
    }

    @Test
    public void testDoublePercent() {
        Assertions.assertEquals("0.00%", doublePercent(0.0D));
        Assertions.assertEquals("100.00%", doublePercent(1.0D));
        Assertions.assertEquals("-100.00%", doublePercent(-1.0D));

        Assertions.assertEquals("20.00%", doublePercent(0.2000D));
        Assertions.assertEquals("20.01%", doublePercent(0.2001D));
        Assertions.assertEquals("20.00%", doublePercent(0.20D));
        Assertions.assertEquals("120.00%", doublePercent(1.20D));
    }
}
