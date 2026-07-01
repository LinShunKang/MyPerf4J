package cn.myperf4j.base.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.util.StrMatchUtils.isMatch;

/**
 * Created by LinShunkang on 2020/07/26
 */
public class StrMatchUtilsTest {

    @Test
    public void testIsMatch() {
        Assertions.assertTrue(isMatch("cn.myperf4j.config.abc", "cn.myperf4j*abc"));
        Assertions.assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*abc"));
        Assertions.assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*a*c"));

        Assertions.assertFalse(isMatch("cn.myperf4j.config.abc", "*.myperf4j*ac"));
        Assertions.assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j*ac"));
        Assertions.assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j.a*"));
    }
}
