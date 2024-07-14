package cn.myperf4j.base.util.text;

import java.text.DecimalFormat;

import static java.lang.ThreadLocal.withInitial;

/**
 * Created by LinShunkang on 2018/7/9
 */
public final class NumFormatUtils {

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = withInitial(() -> new DecimalFormat("0.00"));

    private static final ThreadLocal<DecimalFormat> PERCENT_FORMAT = withInitial(() -> new DecimalFormat("0.00%"));

    public static String doubleFormat(double num) {
        return DECIMAL_FORMAT.get().format(num);
    }

    public static String doublePercent(double num) {
        return PERCENT_FORMAT.get().format(num);
    }

    private NumFormatUtils() {
        //empty
    }
}
