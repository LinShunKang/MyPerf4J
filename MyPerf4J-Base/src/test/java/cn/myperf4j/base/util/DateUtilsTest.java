package cn.myperf4j.base.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertTrue(isSameDay(new Date(), new Date()));
        Assertions.assertFalse(isSameDay(new Date(), new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)));
    }

    @Test
    public void testSameMinute() {
        Assertions.assertTrue(isSameMinute(new Date(), new Date()));
        Assertions.assertFalse(isSameMinute(new Date(), new Date(System.currentTimeMillis() + 60 * 1000)));
    }

    @Test
    public void testSameHour() {
        Assertions.assertTrue(isSameHour(new Date(), new Date()));
        Assertions.assertFalse(isSameHour(new Date(), new Date(System.currentTimeMillis() + 60 * 60 * 1000)));
    }

}
