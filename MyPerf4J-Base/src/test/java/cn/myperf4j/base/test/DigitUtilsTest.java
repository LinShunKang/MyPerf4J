package cn.myperf4j.base.test;

import org.junit.Assert;
import org.junit.Test;

import static cn.myperf4j.base.util.DigitUtils.stringSize;

/**
 * Created by LinShunkang on 2026/06/19
 */
public class DigitUtilsTest {

    @Test
    public void testStringSizeInt() {
        int[] nums = {
                0, 1, 9, 10, 99, 100, 999, 1000,
                -1, -9, -10, -99, -100, -999, -1000,
                Integer.MAX_VALUE, Integer.MIN_VALUE
        };

        for (int num : nums) {
            Assert.assertEquals(String.valueOf(num).length(), stringSize(num));
        }
    }

    @Test
    public void testStringSizeLong() {
        long[] nums = {
                0L, 1L, 9L, 10L, 99L, 100L, 999L, 1000L,
                -1L, -9L, -10L, -99L, -100L, -999L, -1000L,
                999999999999999999L, 1000000000000000000L,
                -999999999999999999L, -1000000000000000000L,
                Long.MAX_VALUE, Long.MIN_VALUE
        };

        for (long num : nums) {
            Assert.assertEquals(String.valueOf(num).length(), stringSize(num));
        }
    }
}
