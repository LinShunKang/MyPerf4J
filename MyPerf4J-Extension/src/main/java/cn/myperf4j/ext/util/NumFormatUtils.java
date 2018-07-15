package cn.myperf4j.ext.util;

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

    public static String getFormatStr(double num) {
        return decimalFormat.get().format(num);
    }

    public static void main(String[] args) {
        System.out.println(getFormatStr(10011.22222D));
        System.out.println(getFormatStr(10011.22D));
        System.out.println(getFormatStr(1.2222D));
        System.out.println(getFormatStr(1.2D));
    }
}
