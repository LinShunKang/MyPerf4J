package MyPerf4J;

import cn.myperf4j.base.util.TypeDescUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

import static cn.myperf4j.base.util.TypeDescUtils.getMethodParamsDesc;
import static cn.myperf4j.base.util.TypeDescUtils.getSimpleClassName;

/**
 * Created by LinShunkang on 2018/10/19
 */
public class TypeDestUtilsTest {

    @Test
    public void test() {
        Assert.assertEquals(getMethodParamsDesc("()V"), "()");
        Assert.assertEquals(getMethodParamsDesc("(IF)V"), "(int, float)");
        Assert.assertEquals(getMethodParamsDesc("(ILjava/lang/Object;F)V"), "(int, Object, float)");
        Assert.assertEquals(getMethodParamsDesc("(Ljava/lang/Object;)I"), "(Object)");
        Assert.assertEquals(getMethodParamsDesc("(ILjava/lang/String;)[I"), "(int, String)");
        Assert.assertEquals(getMethodParamsDesc("(ILjava/lang/Map;)[I"), "(int, Map)");
        Assert.assertEquals(getMethodParamsDesc("([I)Ljava/lang/Object;"), "(int[])");
        Assert.assertEquals(getMethodParamsDesc(
                "([ILjava/lang/Object;[Ljava/lang/Object;[Ljava/lang/String;)Ljava/lang/Object;"),
                "(int[], Object, Object[], String[])");
        Assert.assertEquals(getMethodParamsDesc(
                "([[ILjava/lang/Object;[[[Ljava/lang/Object;[[[[[Ljava/lang/String;)Ljava/lang/Object;"),
                "(int[][], Object, Object[][][], String[][][][][])");
    }

    @Test
    public void testMethod() throws NoSuchMethodException {
        Method method0 = TypeDestUtilsTest.class.getDeclaredMethod("testMethod");
        Assert.assertEquals(getMethodParamsDesc(method0), "()");

        Method method1 = TypeDescUtils.class.getDeclaredMethod("getMethodParamsDesc", Method.class);
        Assert.assertEquals(getMethodParamsDesc(method1), "(Method)");
    }

    @Test
    public void testSimpleClassName() {
        Assert.assertEquals(getSimpleClassName("java/lang/String"), "String");
        Assert.assertEquals(getSimpleClassName("java/lang/Object"), "Object");
    }

}
