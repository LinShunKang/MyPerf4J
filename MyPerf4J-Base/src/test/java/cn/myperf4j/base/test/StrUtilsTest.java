package cn.myperf4j.base.test;

import cn.myperf4j.base.util.StrUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2019/05/12
 */
public class StrUtilsTest {

    @Test
    public void testBlank() {
        Assert.assertTrue(StrUtils.isBlank(" "));
        Assert.assertTrue(StrUtils.isBlank("\t"));
        Assert.assertTrue(StrUtils.isBlank("\n"));
        Assert.assertTrue(StrUtils.isBlank(""));
        Assert.assertTrue(StrUtils.isBlank(null));
        Assert.assertFalse(StrUtils.isBlank("a"));
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue(StrUtils.isEmpty(""));
        Assert.assertTrue(StrUtils.isEmpty(null));
        Assert.assertFalse(StrUtils.isEmpty("a"));
    }
}
