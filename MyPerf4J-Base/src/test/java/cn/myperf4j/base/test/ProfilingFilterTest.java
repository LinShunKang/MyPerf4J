package cn.myperf4j.base.test;

import cn.myperf4j.base.config.ProfilingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInjectClassLoader;
import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInjectMethod;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        ProfilingFilter.addAllAnnotationScanPackages(Arrays.asList(
                "org.annotation",
                "cn.annotation.[service,controller].*"
        ));
        ProfilingFilter.addIncludeAnnotation("cn.myapp.Trace");
    }

    @Test
    public void testIncludePackageMatch() {
        assertFalse(ProfilingFilter.isNeedInject(null));
        assertFalse(ProfilingFilter.isNeedInject("org.junit.Before"));
        assertTrue(ProfilingFilter.isNeedInject("org/junit/Before"));

        assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/a"));
        assertTrue(ProfilingFilter.isNeedInject("cn/junit/test/2"));
        assertFalse(ProfilingFilter.isNeedInject("cn/junit/test2"));
    }

    @Test
    public void testIncludePackageExpressionExpand() {
        assertTrue(ProfilingFilter.isNeedInject("cn/myapp/service/UserService"));
        assertTrue(ProfilingFilter.isNeedInject("cn/myapp/controller/UserController"));
        assertFalse(ProfilingFilter.isNeedInject("cn/myapp/repository/UserRepository"));
    }

    @Test
    public void testBuiltInIncludePackageMatch() {
        assertTrue(ProfilingFilter.isNeedInject("org/apache/ibatis/binding/MapperProxy"));
        assertTrue(ProfilingFilter.isNeedInject("com/alibaba/dubbo/rpc/proxy/InvokerInvocationHandler"));
        assertFalse(ProfilingFilter.isNeedInject("org/apache/ibatis/session/SqlSession"));
    }

    @Test
    public void testExcludePackageMatch() {
        assertTrue(ProfilingFilter.isNotNeedInject(null));
        assertTrue(ProfilingFilter.isNotNeedInject("org/junit/rules/ErrorCollector"));

        assertFalse(ProfilingFilter.isNotNeedInject("com/junit/test2"));
        assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/a"));
        assertTrue(ProfilingFilter.isNotNeedInject("com/junit/test/2"));
        assertTrue(ProfilingFilter.isNotNeedInject("cn/myapp/internal/SecretService"));
    }

    @Test
    public void testBuiltInExcludePackageMatch() {
        assertTrue(ProfilingFilter.isNotNeedInject("java/lang/String"));
        assertTrue(ProfilingFilter.isNotNeedInject("javax/servlet/Filter"));
        assertTrue(ProfilingFilter.isNotNeedInject("sun/misc/Unsafe"));
        assertTrue(ProfilingFilter.isNotNeedInject("com/sun/proxy/$Proxy0"));
        assertTrue(ProfilingFilter.isNotNeedInject("com/intellij/rt/ExecutionException"));
        assertTrue(ProfilingFilter.isNotNeedInject("cn/myperf4j/base/config/ProfilingFilter"));
        assertFalse(ProfilingFilter.isNotNeedInject("org/junit/Before"));
    }

    @Test
    public void testInnerClassIsNotNeedInject() {
        assertTrue(ProfilingFilter.isNotNeedInject("cn/junit/test/Foo$Bar"));
        assertTrue(ProfilingFilter.isNotNeedInject("com/sun/proxy/$Proxy0"));
    }

    @Test
    public void testMethodFilter() {
        assertFalse(isNotNeedInjectMethod(null));
        assertTrue(isNotNeedInjectMethod("toString"));
        assertTrue(isNotNeedInjectMethod("hello"));
        assertFalse(isNotNeedInjectMethod("assertFalse"));

        assertFalse(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB)"));
        assertTrue(isNotNeedInjectMethod("Demo.getId1()"));
        assertTrue(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB,long)"));
        assertTrue(isNotNeedInjectMethod("Demo.getId1(long)"));
        assertTrue(isNotNeedInjectMethod("Demo.getId1(long,int)"));
    }

    @Test
    public void testSpecialMethodFilter() {
        assertTrue(isNotNeedInjectMethod("lambda$main$0"));
        assertTrue(isNotNeedInjectMethod("lambda$main$0()"));
        assertFalse(isNotNeedInjectMethod("Demo.getId1(ClassA$ClassB)"));
    }

    @Test
    public void testClassLoaderFilter() {
        assertTrue(isNotNeedInjectClassLoader("org.apache.catalina.loader.WebappClassLoader"));
        assertFalse(isNotNeedInjectClassLoader("org.springframework.boot.loader.LaunchedURLClassLoader"));
    }

    @Test
    public void testAnnotationScanPackageMatch() {
        assertFalse(ProfilingFilter.isNeedScanAnnotation(null));
        assertFalse(ProfilingFilter.isNeedScanAnnotation("org.annotation.Trace"));
        assertTrue(ProfilingFilter.isNeedScanAnnotation("org/annotation/Trace"));

        assertTrue(ProfilingFilter.isNeedScanAnnotation("cn/annotation/service/UserService"));
        assertTrue(ProfilingFilter.isNeedScanAnnotation("cn/annotation/controller/UserController"));
        assertFalse(ProfilingFilter.isNeedScanAnnotation("cn/annotation/repository/UserRepository"));
    }

    @Test
    public void testIncludeAnnotation() {
        assertTrue(ProfilingFilter.isNeedInjectAnnotation("Lcn/myapp/Trace;"));
        assertFalse(ProfilingFilter.isNeedInjectAnnotation("Lcn/myapp/Metric;"));
    }
}
