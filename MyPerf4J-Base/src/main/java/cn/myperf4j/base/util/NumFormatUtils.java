package cn.myperf4j.base.util;

import java.text.DecimalFormat;

/**
 * Created by LinShunkang on 2018/7/9
 */
public final class NumFormatUtils {

    private static final ThreadLocal<DecimalFormat> decimalFormat = new ThreadLocal<DecimalFormat>(){
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.00");
        }
    };

    public static String formatDouble(double num) {
        return decimalFormat.get().format(num);
    }
}
