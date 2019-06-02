package cn.myperf4j.base.test;

import cn.myperf4j.base.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2019/05/12
 */
public class StringUtilsTest {

    @Test
    public void testBlank() {
        Assert.assertTrue(StringUtils.isBlank(" "));
        Assert.assertTrue(StringUtils.isBlank("\t"));
        Assert.assertTrue(StringUtils.isBlank("\n"));
        Assert.assertTrue(StringUtils.isBlank(""));
        Assert.assertTrue(StringUtils.isBlank(null));
        Assert.assertFalse(StringUtils.isBlank("a"));
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertFalse(StringUtils.isEmpty("a"));
    }
}
