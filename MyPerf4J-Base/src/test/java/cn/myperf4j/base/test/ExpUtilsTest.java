package cn.myperf4j.base.test;

import cn.myperf4j.base.util.PkgExpUtils;
import cn.myperf4j.base.util.StrMatchUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2019-01-01
 */
public class ExpUtilsTest {

    @Test
    public void testParse() {
        Assertions.assertTrue(PkgExpUtils.parse("cn.myperf4j.util.[Logger,DateUtils]")
                .containsAll(Arrays.asList("cn.myperf4j.util.Logger;cn.myperf4j.util.DateUtils".split(";"))));

        Assertions.assertTrue(PkgExpUtils.parse("cn.myperf4j.util.[Logger,DateUtils].abc")
                .containsAll(Arrays.asList("cn.myperf4j.util.Logger.abc;cn.myperf4j.util.DateUtils.abc".split(";"))));

        Assertions.assertTrue(PkgExpUtils.parse("cn.myperf4j.util.[Logger,DateUtils].abc.[e,f]")
                .containsAll(Arrays.asList(("cn.myperf4j.util.Logger.abc.e;cn.myperf4j.util.DateUtils.abc.f;" +
                        "cn.myperf4j.util.Logger.abc.e;cn.myperf4j.util.DateUtils.abc.f").split(";"))));
    }

    @Test
    public void testMatch() {
        Assertions.assertTrue(StrMatchUtils.isMatch("abc", "abc"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abc", "a*"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abc", "*abc"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abc", "a*bc"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abc", "a*c"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abc", "ab*"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abc", "ab**"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abcdef", "ab****c*"));
        Assertions.assertTrue(StrMatchUtils.isMatch("abcdef", "ab*c*f"));

        Assertions.assertFalse(StrMatchUtils.isMatch("abcdef", "ab*c*eg"));
        Assertions.assertFalse(StrMatchUtils.isMatch("abcdef", "abcdefg"));
    }
}
