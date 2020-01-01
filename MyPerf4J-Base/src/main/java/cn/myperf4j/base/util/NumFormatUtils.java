package cn.myperf4j.base.util;

import java.text.DecimalFormat;

/**
 * Created by LinShunkang on 2018/7/9
 */
public final class NumFormatUtils {

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.00");
        }
    };

    private static final ThreadLocal<DecimalFormat> PERCENT_FORMAT = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.00%");
        }
    };

    public static String doubleFormat(double num) {
        return DECIMAL_FORMAT.get().format(num);
    }

    public static String doublePercent(double num) {
        return PERCENT_FORMAT.get().format(num);
    }
}
