package cn.myperf4j.base.test;

import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyKeys.Basic;
import cn.myperf4j.base.constant.PropertyKeys.Filter;
import cn.myperf4j.base.constant.PropertyKeys.Metrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class MyPropertiesTest extends BaseTest {

    @Test
    public void test() {
        Assertions.assertEquals(BaseTest.TEMP_FILE, MyProperties.getStr(PropertyKeys.PRO_FILE_NAME));
        Assertions.assertEquals(BaseTest.APP_NAME, MyProperties.getStr(Basic.APP_NAME));
        Assertions.assertEquals(BaseTest.METRICS_EXPORTER, MyProperties.getStr(Metrics.EXPORTER));
        Assertions.assertEquals(BaseTest.INCLUDE_PACKAGES, MyProperties.getStr(Filter.PACKAGES_INCLUDE));

        MyProperties.setStr("key", "value");
        Assertions.assertEquals("value", MyProperties.getStr("key"));
        Assertions.assertTrue(MyProperties.isSame("key", "value"));

        MyProperties.setStr("long", "1000");
        Assertions.assertEquals(1000, MyProperties.getLong("long", 1));
        Assertions.assertEquals(10000, MyProperties.getLong("long", 1, 10000));
    }
}
