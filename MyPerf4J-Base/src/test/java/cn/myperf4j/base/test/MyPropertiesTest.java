package cn.myperf4j.base.test;

import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyKeys.Basic;
import cn.myperf4j.base.constant.PropertyKeys.Filter;
import cn.myperf4j.base.constant.PropertyKeys.Metrics;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class MyPropertiesTest extends BaseTest {

    @Test
    public void test() {
        Assert.assertEquals(MyProperties.getStr(PropertyKeys.PRO_FILE_NAME), BaseTest.TEMP_FILE);
        Assert.assertEquals(MyProperties.getStr(Basic.APP_NAME), BaseTest.APP_NAME);
        Assert.assertEquals(MyProperties.getStr(Metrics.EXPORTER), BaseTest.METRICS_EXPORTER);
        Assert.assertEquals(MyProperties.getStr(Filter.PACKAGES_INCLUDE), BaseTest.INCLUDE_PACKAGES);


        MyProperties.setStr("key", "value");
        Assert.assertEquals(MyProperties.getStr("key"), "value");
        Assert.assertTrue(MyProperties.isSame("key", "value"));

        MyProperties.setStr("long", "1000");
        Assert.assertEquals(MyProperties.getLong("long", 1), 1000);
        Assert.assertEquals(MyProperties.getLong("long", 1, 10000), 10000);
    }

}