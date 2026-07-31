package cn.myperf4j.base.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static cn.myperf4j.base.util.StrUtils.isBlank;
import static cn.myperf4j.base.util.StrUtils.isEmpty;
import static cn.myperf4j.base.util.StrUtils.splitAsList;

/**
 * Created by LinShunkang on 2019/05/12
 */
public class StrUtilsTest {

    @Test
    public void testBlank() {
        Assertions.assertTrue(isBlank(" "));
        Assertions.assertTrue(isBlank("\t"));
        Assertions.assertTrue(isBlank("\n"));
        Assertions.assertTrue(isBlank(""));
        Assertions.assertTrue(isBlank(null));
        Assertions.assertFalse(isBlank("a"));
    }

    @Test
    public void testEmpty() {
        Assertions.assertTrue(isEmpty(""));
        Assertions.assertTrue(isEmpty(null));
        Assertions.assertFalse(isEmpty("a"));
    }

    @Test
    public void testSplitAsList() {
        Assertions.assertEquals(Arrays.asList("A", "B", "C"), splitAsList("A,B,C", ','));
        Assertions.assertEquals(Arrays.asList("A", "B", "C"), splitAsList("A,B,C,", ','));
        Assertions.assertEquals(Arrays.asList("A", "B", "C"), splitAsList(",A,B,C,", ','));
        Assertions.assertEquals(Arrays.asList("A", "B", "C"), splitAsList(",,,,A,B,C,,,,", ','));
    }
}
