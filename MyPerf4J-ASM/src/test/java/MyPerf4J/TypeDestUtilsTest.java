package MyPerf4J;

import cn.myperf4j.base.util.TypeDescUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static cn.myperf4j.base.util.TypeDescUtils.getMethodParamsDesc;
import static cn.myperf4j.base.util.TypeDescUtils.getSimpleClassName;

/**
 * Created by LinShunkang on 2018/10/19
 */
public class TypeDestUtilsTest {

    @Test
    public void test() {
        Assertions.assertEquals("()", getMethodParamsDesc("()V"));
        Assertions.assertEquals("(int, float)", getMethodParamsDesc("(IF)V"));
        Assertions.assertEquals("(int, Object, float)", getMethodParamsDesc("(ILjava/lang/Object;F)V"));
        Assertions.assertEquals("(Object)", getMethodParamsDesc("(Ljava/lang/Object;)I"));
        Assertions.assertEquals("(int, String)", getMethodParamsDesc("(ILjava/lang/String;)[I"));
        Assertions.assertEquals("(int, Map)", getMethodParamsDesc("(ILjava/lang/Map;)[I"));
        Assertions.assertEquals("(int[])", getMethodParamsDesc("([I)Ljava/lang/Object;"));
        Assertions.assertEquals("(int[], Object, Object[], String[])",
                getMethodParamsDesc(
                        "([ILjava/lang/Object;[Ljava/lang/Object;[Ljava/lang/String;)Ljava/lang/Object;"));
        Assertions.assertEquals("(int[][], Object, Object[][][], String[][][][][])",
                getMethodParamsDesc(
                        "([[ILjava/lang/Object;[[[Ljava/lang/Object;[[[[[Ljava/lang/String;)Ljava/lang/Object;"));
    }

    @Test
    public void testMethod() throws NoSuchMethodException {
        Method method0 = TypeDestUtilsTest.class.getDeclaredMethod("testMethod");
        Assertions.assertEquals("()", getMethodParamsDesc(method0));

        Method method1 = TypeDescUtils.class.getDeclaredMethod("getMethodParamsDesc", Method.class);
        Assertions.assertEquals("(Method)", getMethodParamsDesc(method1));
    }

    @Test
    public void testSimpleClassName() {
        Assertions.assertEquals("String", getSimpleClassName("java/lang/String"));
        Assertions.assertEquals("Object", getSimpleClassName("java/lang/Object"));
    }
}
