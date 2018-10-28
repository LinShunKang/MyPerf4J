package cn.myperf4j.base.test;

import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.constant.PropertyKeys;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class MyPropertiesTest extends BaseTest {

    @Test
    public void test() {
        Assert.assertEquals(MyProperties.getStr(PropertyKeys.PRO_FILE_NAME), BaseTest.TEMP_FILE);
        Assert.assertEquals(MyProperties.getStr(PropertyKeys.APP_NAME), BaseTest.APP_NAME);
        Assert.assertEquals(MyProperties.getInt(PropertyKeys.METRICS_PROCESS_TYPE, -1), BaseTest.METRICS_PROCESSOR_TYPE);
        Assert.assertEquals(MyProperties.getStr(PropertyKeys.FILTER_INCLUDE_PACKAGES), BaseTest.INCLUDE_PACKAGES);
        Assert.assertEquals(MyProperties.getInt(PropertyKeys.MILLI_TIME_SLICE, -1), BaseTest.MILLI_TIMES_LICE);


        MyProperties.setStr("key", "value");
        Assert.assertEquals(MyProperties.getStr("key"), "value");
        Assert.assertTrue(MyProperties.isSame("key", "value"));

        MyProperties.setStr("long", "1000");
        Assert.assertEquals(MyProperties.getLong("long", 1), 1000);
        Assert.assertEquals(MyProperties.getLong("long", 1, 10000), 10000);
    }

}