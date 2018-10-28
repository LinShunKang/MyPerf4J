package cn.myperf4j.base.test;

import cn.myperf4j.base.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class DateUtilsTest {

    @Test
    public void test() {
        Assert.assertTrue(DateUtils.isSameMinute(new Date(), new Date()));
        Assert.assertTrue(DateUtils.isSameHour(new Date(), new Date()));
        Assert.assertTrue(DateUtils.isSameDay(new Date(), new Date()));

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MINUTE, 1);
        Assert.assertFalse(DateUtils.isSameMinute(new Date(), calendar1.getTime()));

        calendar1.add(Calendar.HOUR_OF_DAY, 1);
        Assert.assertFalse(DateUtils.isSameHour(new Date(), calendar1.getTime()));

        calendar1.add(Calendar.DATE, 1);
        Assert.assertFalse(DateUtils.isSameDay(new Date(), calendar1.getTime()));
    }
}
