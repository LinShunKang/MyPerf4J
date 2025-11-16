package cn.myperf4j.base.test;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertTrue(isBlank(" "));
        Assert.assertTrue(isBlank("\t"));
        Assert.assertTrue(isBlank("\n"));
        Assert.assertTrue(isBlank(""));
        Assert.assertTrue(isBlank(null));
        Assert.assertFalse(isBlank("a"));
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue(isEmpty(""));
        Assert.assertTrue(isEmpty(null));
        Assert.assertFalse(isEmpty("a"));
    }

    @Test
    public void testSplitAsList() {
        Assert.assertEquals(Arrays.asList("A", "B", "C"), splitAsList("A,B,C", ','));
        Assert.assertEquals(Arrays.asList("A", "B", "C"), splitAsList("A,B,C,", ','));
        Assert.assertEquals(Arrays.asList("A", "B", "C"), splitAsList(",A,B,C,", ','));
        Assert.assertEquals(Arrays.asList("A", "B", "C"), splitAsList(",,,,A,B,C,,,,", ','));
    }
}
