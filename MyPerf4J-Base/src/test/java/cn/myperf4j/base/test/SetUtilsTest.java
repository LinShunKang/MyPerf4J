package cn.myperf4j.base.test;

import cn.myperf4j.base.util.collections.SetUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by LinShunkang on 2019-01-01
 */
public class SetUtilsTest {

    @Test
    public void test() {
        Assertions.assertTrue(SetUtils.of(1).contains(1));
        Assertions.assertTrue(SetUtils.of(1, 2, 3).contains(1));
        Assertions.assertTrue(SetUtils.of(1, 2, 3).contains(2));
        Assertions.assertTrue(SetUtils.of(1, 2, 3).contains(3));
    }
}
