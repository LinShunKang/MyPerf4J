package cn.myperf4j.base.test;

import cn.myperf4j.base.util.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/10/28
 */
public class DateUtilsTest {

    @Test
    public void test() {
        Assertions.assertTrue(DateUtils.isSameMinute(new Date(), new Date()));
        Assertions.assertTrue(DateUtils.isSameHour(new Date(), new Date()));
        Assertions.assertTrue(DateUtils.isSameDay(new Date(), new Date()));

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MINUTE, 1);
        Assertions.assertFalse(DateUtils.isSameMinute(new Date(), calendar1.getTime()));

        calendar1.add(Calendar.HOUR_OF_DAY, 1);
        Assertions.assertFalse(DateUtils.isSameHour(new Date(), calendar1.getTime()));

        calendar1.add(Calendar.DATE, 1);
        Assertions.assertFalse(DateUtils.isSameDay(new Date(), calendar1.getTime()));
    }
}
