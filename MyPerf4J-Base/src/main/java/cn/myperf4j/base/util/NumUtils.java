package cn.myperf4j.base.util;

/**
 * Created by LinShunkang on 2019/11/03
 */
public final class NumUtils {

    private NumUtils() {
        //empty
    }

    public static double getPercent(double curNum, double maxNum) {
        if (curNum > 0L && maxNum > 0L) {
            return (100D * curNum) / maxNum;
        }
        return 0D;
    }

    public static int parseInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            Logger.error("NumUtils.parseInt(" + str + ", " + defaultValue + ")", e);
        }
        return defaultValue;
    }

    public static long composeKv(int key, int value) {
        return ((long) value) << 32 | key;
    }

    public static int parseKey(long kv) {
        return (int) kv;
    }

    public static int parseValue(long kv) {
        return (int) (kv >> 32);
    }
}
