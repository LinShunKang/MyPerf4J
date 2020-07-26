package cn.myperf4j.base.util;

import org.junit.Assert;
import org.junit.Test;

import static cn.myperf4j.base.util.StrMatchUtils.isMatch;

/**
 * Created by LinShunkang on 2020/07/26
 */
public class StrMatchUtilsTest {

    @Test
    public void testIsMatch() {
        Assert.assertTrue(isMatch("cn.myperf4j.config.abc", "cn.myperf4j*abc"));
        Assert.assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*abc"));
        Assert.assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*a*c"));

        Assert.assertFalse(isMatch("cn.myperf4j.config.abc", "*.myperf4j*ac"));
        Assert.assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j*ac"));
        Assert.assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j.a*"));
    }

}
