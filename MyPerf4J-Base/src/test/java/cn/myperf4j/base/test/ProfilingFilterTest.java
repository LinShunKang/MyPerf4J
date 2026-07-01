package cn.myperf4j.base.test;

import cn.myperf4j.base.config.ProfilingFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInjectClassLoader;
import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInjectMethod;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class ProfilingFilterTest {

    @BeforeEach
    public void init() {
        ProfilingFilter.addIncludePackage("org.junit");
        ProfilingFilter.addExcludePackage("org.junit.rules");
        ProfilingFilter.addExcludeMethods("hello");
        ProfilingFilter.addExcludeClassLoader("org.apache.catalina.loader.WebappClassLoader");
        ProfilingFilter.addExcludeMethods("Demo.getId1(long)");
        ProfilingFilter.addExcludeMethods("Demo.getId1(long,int)");
        ProfilingFilter.addExcludeMethods("Demo.getId1()");
        ProfilingFilter.addExcludeMethods("Demo.getId1(ClassA$ClassB,long)");
    }

    @Test
    public void test() {
        Assertions.assertFalse(ProfilingFilter.isNeedInject("org.junit.Before"));
        Assertions.assertTrue(ProfilingFilter.isNeedInject("org/junit/Before"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("org/junit/rules/ErrorCollector"));

        Assertions.assertTrue(isNotNeedInjectMethod("toString"));
        Assertions.assertTrue(isNotNeedInjectMethod("hello"));
        Assertions.assertFalse(isNotNeedInjectMethod("assertFalse"));

        Assertions.assertFalse(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB)"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1()"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB,long)"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1(long)"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1(long,int)"));

        Assertions.assertTrue(isNotNeedInjectClassLoader("org.apache.catalina.loader.WebappClassLoader"));
        Assertions.assertFalse(isNotNeedInjectClassLoader("org.springframework.boot.loader.LaunchedURLClassLoader"));
    }

    @Test
    public void testWildcardMatch() {
        Assertions.assertFalse(ProfilingFilter.isNeedInject("cn/junit/test/a"));
        Assertions.assertFalse(ProfilingFilter.isNeedInject("cn/junit/test2"));

        ProfilingFilter.addIncludePackage("cn.junit.test.*");
        Assertions.assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/a"));
        Assertions.assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/2"));

        Assertions.assertFalse(ProfilingFilter.isNotNeedInject("com/junit/test/a"));
        Assertions.assertFalse(ProfilingFilter.isNotNeedInject("com/junit/test2"));

        ProfilingFilter.addExcludePackage("com.junit.test.*");
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/a"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/2"));
    }
}
