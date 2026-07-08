package cn.myperf4j.base.test;

import cn.myperf4j.base.config.ProfilingFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInjectClassLoader;
import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInjectMethod;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class ProfilingFilterTest {

    @BeforeAll
    public static void init() {
        ProfilingFilter.addAllIncludePackage(Arrays.asList(
                "org.junit",
                "cn.junit.test.*",
                "cn.myapp.[service,controller].*"
        ));
        ProfilingFilter.addAllExcludePackage(Arrays.asList(
                "org.junit.rules",
                "com.junit.test.*",
                "cn.myapp.internal.*"
        ));
        ProfilingFilter.addExcludeMethods("hello");
        ProfilingFilter.addExcludeMethods("Demo.getId1(long)");
        ProfilingFilter.addExcludeMethods("Demo.getId1(long,int)");
        ProfilingFilter.addExcludeMethods("Demo.getId1()");
        ProfilingFilter.addExcludeMethods("Demo.getId1(ClassA$ClassB,long)");
        ProfilingFilter.addExcludeClassLoader("org.apache.catalina.loader.WebappClassLoader");
    }

    @Test
    public void testIncludePackageMatch() {
        Assertions.assertFalse(ProfilingFilter.isNeedInject(null));
        Assertions.assertFalse(ProfilingFilter.isNeedInject("org.junit.Before"));
        Assertions.assertTrue(ProfilingFilter.isNeedInject("org/junit/Before"));

        Assertions.assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/a"));
        Assertions.assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/2"));
        Assertions.assertFalse(ProfilingFilter.isNeedInject("cn/junit/test2"));
    }

    @Test
    public void testIncludePackageExpressionExpand() {
        Assertions.assertTrue(ProfilingFilter.isNeedInject("cn/myapp/service/UserService"));
        Assertions.assertTrue(ProfilingFilter.isNeedInject("cn/myapp/controller/UserController"));
        Assertions.assertFalse(ProfilingFilter.isNeedInject("cn/myapp/repository/UserRepository"));
    }

    @Test
    public void testBuiltInIncludePackageMatch() {
        Assertions.assertTrue(ProfilingFilter.isNeedInject("org/apache/ibatis/binding/MapperProxy"));
        Assertions.assertTrue(ProfilingFilter.isNeedInject("com/alibaba/dubbo/rpc/proxy/InvokerInvocationHandler"));
        Assertions.assertFalse(ProfilingFilter.isNeedInject("org/apache/ibatis/session/SqlSession"));
    }

    @Test
    public void testExcludePackageMatch() {
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject(null));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("org/junit/rules/ErrorCollector"));

        Assertions.assertFalse(ProfilingFilter.isNotNeedInject("com/junit/test2"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/a"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/2"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("cn/myapp/internal/SecretService"));
    }

    @Test
    public void testBuiltInExcludePackageMatch() {
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("java/lang/String"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("javax/servlet/Filter"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("sun/misc/Unsafe"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("com/sun/proxy/$Proxy0"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("com/intellij/rt/ExecutionException"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("cn/myperf4j/base/config/ProfilingFilter"));
        Assertions.assertFalse(ProfilingFilter.isNotNeedInject("org/junit/Before"));
    }

    @Test
    public void testInnerClassIsNotNeedInject() {
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("cn/junit/test/Foo$Bar"));
        Assertions.assertTrue(ProfilingFilter.isNotNeedInject("com/sun/proxy/$Proxy0"));
    }

    @Test
    public void testMethodFilter() {
        Assertions.assertFalse(isNotNeedInjectMethod(null));
        Assertions.assertTrue(isNotNeedInjectMethod("toString"));
        Assertions.assertTrue(isNotNeedInjectMethod("hello"));
        Assertions.assertFalse(isNotNeedInjectMethod("assertFalse"));

        Assertions.assertFalse(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB)"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1()"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB,long)"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1(long)"));
        Assertions.assertTrue(isNotNeedInjectMethod("Demo.getId1(long,int)"));
    }

    @Test
    public void testSpecialMethodFilter() {
        Assertions.assertTrue(isNotNeedInjectMethod("lambda$main$0"));
        Assertions.assertTrue(isNotNeedInjectMethod("lambda$main$0()"));
        Assertions.assertFalse(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB)"));
    }

    @Test
    public void testClassLoaderFilter() {
        Assertions.assertTrue(isNotNeedInjectClassLoader("org.apache.catalina.loader.WebappClassLoader"));
        Assertions.assertFalse(isNotNeedInjectClassLoader("org.springframework.boot.loader.LaunchedURLClassLoader"));
    }
}
