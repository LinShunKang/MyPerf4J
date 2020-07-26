package cn.myperf4j.base.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static cn.myperf4j.base.util.DateUtils.isSameDay;
import static cn.myperf4j.base.util.DateUtils.isSameHour;
import static cn.myperf4j.base.util.DateUtils.isSameMinute;

/**
 * Created by LinShunkang on 2020/07/26
 */
public class DateUtilsTest {

    @Test
    public void testSameDay() {
        Assert.assertTrue(isSameDay(new Date(), new Date()));
        Assert.assertFalse(isSameDay(new Date(), new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)));
    }

    @Test
    public void testSameMinute() {
        Assert.assertTrue(isSameMinute(new Date(), new Date()));
        Assert.assertFalse(isSameMinute(new Date(), new Date(System.currentTimeMillis() + 60 * 1000)));
    }

    @Test
    public void testSameHour() {
        Assert.assertTrue(isSameHour(new Date(), new Date()));
        Assert.assertFalse(isSameHour(new Date(), new Date(System.currentTimeMillis() + 60 * 60 * 1000)));
    }

}
