package cn.myperf4j.base.test;

import cn.myperf4j.base.util.text.DateFormatUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class DateFormatUtilsTest {

    @Test
    public void testFormatToSeconds() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final long mills = System.currentTimeMillis();
        Assertions.assertEquals(format.format(new Date(mills)), DateFormatUtils.formatToSeconds(mills));
    }

    @Test
    public void testFormatToMillis() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        final long mills = System.currentTimeMillis();
        Assertions.assertEquals(format.format(new Date(mills)), DateFormatUtils.formatToMillis(mills));
    }
}
