package cn.myperf4j.base.test;

import cn.myperf4j.base.util.SetUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2019-01-01
 */
public class SetUtilsTest {

    @Test
    public void test() {
        Assert.assertTrue(SetUtils.of(1).contains(1));
        Assert.assertTrue(SetUtils.of(1, 2, 3).contains(1));
        Assert.assertTrue(SetUtils.of(1, 2, 3).contains(2));
        Assert.assertTrue(SetUtils.of(1, 2, 3).contains(3));
    }
}
