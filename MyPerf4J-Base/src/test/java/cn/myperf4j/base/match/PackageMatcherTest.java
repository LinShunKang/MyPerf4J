package cn.myperf4j.base.match;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LinShunkang on 2026/07/07
 */
public class PackageMatcherTest {

    @Test
    public void testPrefixMatch() {
        final PackageMatcher matcher = new PackageMatcher();
        matcher.init(setOf("java/", "javax/", "cn/myperf4j/"));

        Assertions.assertTrue(matcher.isMatch("java/lang/String"));
        Assertions.assertTrue(matcher.isMatch("javax/servlet/Filter"));
        Assertions.assertTrue(matcher.isMatch("cn/myperf4j/base/config/ProfilingFilter"));
        Assertions.assertFalse(matcher.isMatch("org/junit/Test"));
    }

    @Test
    public void testWildcardMatchWithSamePrefix() {
        final PackageMatcher matcher = new PackageMatcher();
        matcher.init(setOf(
                "cn/myapp/service/*Service",
                "cn/myapp/service/*Repository",
                "cn/myapp/controller/*Controller"
        ));

        Assertions.assertTrue(matcher.isMatch("cn/myapp/service/UserService"));
        Assertions.assertTrue(matcher.isMatch("cn/myapp/service/UserRepository"));
        Assertions.assertTrue(matcher.isMatch("cn/myapp/controller/UserController"));
    }

    @Test
    public void testWildcardDoesNotMatchOnlyByTriePrefix() {
        final PackageMatcher matcher = new PackageMatcher();
        matcher.init(setOf(
                "cn/myapp/service/*Service",
                "cn/myapp/service/*Repository"
        ));

        Assertions.assertFalse(matcher.isMatch("cn/myapp/service/UserController"));
        Assertions.assertFalse(matcher.isMatch("cn/myapp/service/UserDao"));
    }

    @Test
    public void testWildcardWithoutLiteralPrefix() {
        final PackageMatcher matcher = new PackageMatcher();
        matcher.init(setOf("*Service", "*Controller"));

        Assertions.assertTrue(matcher.isMatch("cn/myapp/service/UserService"));
        Assertions.assertTrue(matcher.isMatch("cn/myapp/controller/UserController"));
        Assertions.assertFalse(matcher.isMatch("cn/myapp/repository/UserRepository"));
    }

    @Test
    public void testEmptyRulesDoNotMatch() {
        final PackageMatcher matcher = new PackageMatcher();
        matcher.init(Collections.emptySet());

        Assertions.assertFalse(matcher.isMatch("cn/myapp/service/UserService"));
    }

    @Test
    public void testCanInitOnlyOnce() {
        final PackageMatcher matcher = new PackageMatcher();
        matcher.init(setOf("cn/myapp/"));

        Assertions.assertThrows(IllegalStateException.class, () -> matcher.init(setOf("org/junit/")));
    }

    private static Set<String> setOf(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}
