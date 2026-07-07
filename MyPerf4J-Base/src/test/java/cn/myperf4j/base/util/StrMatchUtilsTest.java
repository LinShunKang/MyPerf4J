package cn.myperf4j.base.util;

import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.util.StrMatchUtils.isMatch;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by LinShunkang on 2020/07/26
 */
public class StrMatchUtilsTest {

    @Test
    public void testExactMatch() {
        assertTrue(isMatch("", ""));
        assertTrue(isMatch("abc", "abc"));
        assertTrue(isMatch("cn/myperf4j/base/Version", "cn/myperf4j/base/Version"));

        assertFalse(isMatch("abc", "ab"));
        assertFalse(isMatch("abc", "abcd"));
        assertFalse(isMatch("abc", "Abc"));
    }

    @Test
    public void testWildcardMatch() {
        assertTrue(isMatch("", "*"));
        assertTrue(isMatch("", "**"));
        assertTrue(isMatch("abc", "*"));
        assertTrue(isMatch("abc", "a*"));
        assertTrue(isMatch("abc", "*c"));
        assertTrue(isMatch("abc", "a*c"));
        assertTrue(isMatch("abc", "a*b*c"));
        assertTrue(isMatch("abc", "a**b**c"));
        assertTrue(isMatch("abcdef", "ab****c*"));
        assertTrue(isMatch("abcdef", "ab*c*f"));
        assertTrue(isMatch("cn/myperf4j/base/Version", "cn/*/base/*"));
        assertTrue(isMatch("cn/myperf4j/base//Version", "cn/*/base/*"));

        assertFalse(isMatch("", "a*"));
        assertFalse(isMatch("abc", "a*d"));
        assertFalse(isMatch("abcdef", "ab*c*eg"));
        assertFalse(isMatch("cn/myperf4j/base/Version", "cn/*/core/*"));
    }

    @Test
    public void testGreedyBacktrackingMatch() {
        assertTrue(isMatch("abcabc", "a*bc"));
        assertTrue(isMatch("abcabc", "a*c"));
        assertTrue(isMatch("mississippi", "m*iss*ppi"));
        assertTrue(isMatch("com/example/service/UserService", "com/*/service/*Service"));

        assertFalse(isMatch("abcabc", "a*bd"));
        assertFalse(isMatch("mississippi", "m*iss*ppx"));
        assertFalse(isMatch("com/example/service/UserController", "com/*/service/*Service"));
    }

    @Test
    public void testPackageStyleMatch() {
        assertTrue(isMatch("cn.myperf4j.config.abc", "cn.myperf4j*abc"));
        assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*abc"));
        assertTrue(isMatch("cn.myperf4j.config.abc", "*.myperf4j*a*c"));

        assertFalse(isMatch("cn.myperf4j.config.abc", "*.myperf4j*ac"));
        assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j*ac"));
        assertFalse(isMatch("cn.myperf4j.config.abc", "a*.myperf4j.a*"));
    }

    @Test
    public void testIsMatchFromStartIndex() {
        assertTrue(isMatch("cn.myperf4j.config.abc", 0, "cn.myperf4j*abc", 0));
        assertTrue(isMatch("cn.myperf4j.config.abc", 1, "cn.myperf4j*abc", 1));
        assertTrue(isMatch("cn.myperf4j.config.abc", 10, "cn.myperf4j*abc", 10));
        assertTrue(isMatch("cn.myperf4j.config.abc", 11, "cn.myperf4j*abc", 11));
        assertTrue(isMatch("cn.myperf4j.config.abc", 19, "cn.myperf4j*abc", 12));

        assertTrue(isMatch("cn.myperf4j.config.abc", 0, "*.myperf4j*abc", 0));
        assertTrue(isMatch("cn.myperf4j.config.abc", 11, "*.myperf4j*abc", 10));
        assertFalse(isMatch("cn.myperf4j.config.abc", 11, "*.myperf4j*abc", 11));

        assertTrue(isMatch("cn.myperf4j.config.abc", 0, "c*.myperf4j.*", 0));
        assertTrue(isMatch("cn.myperf4j.config.abc", 1, "c*.myperf4j.*", 1));
        assertTrue(isMatch("cn.myperf4j.config.abc", 2, "c*.myperf4j.*", 2));
        assertTrue(isMatch("cn.myperf4j.config.abc", 3, "c*.myperf4j.*", 3));
        assertTrue(isMatch("cn.myperf4j.config.abc", 12, "c*.myperf4j.*", 12));
        assertTrue(isMatch("cn.myperf4j.config.abc", 13, "c*.myperf4j.*", 12));
        assertFalse(isMatch("cn.myperf4j.config.abc", 13, "c*.myperf4j.*", 13));
    }

    @Test
    public void testIsMatchFromPrefixLength() {
        assertTrue(isMatch("com/example/service/UserService",
                "com/example/service/".length(),
                "com/example/service/*Service",
                "com/example/service/".length()));
        assertFalse(isMatch("com/example/service/UserController",
                "com/example/service/".length(),
                "com/example/service/*Service",
                "com/example/service/".length()));
        assertTrue(isMatch("com/example/dao/UserRepository",
                "com/example/dao/".length(),
                "com/example/dao/*Repository",
                "com/example/dao/".length()));
    }

    @Test
    public void testIsMatchFromEndIndex() {
        assertTrue(isMatch("abc", 3, "abc", 3));
        assertTrue(isMatch("abc", 3, "abc*", 3));
        assertTrue(isMatch("abc", 3, "abc**", 3));

        assertFalse(isMatch("abc", 3, "abcd", 3));
        assertFalse(isMatch("abc", 3, "abc*d", 3));
    }
}
