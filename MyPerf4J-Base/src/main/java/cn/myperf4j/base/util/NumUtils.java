package cn.myperf4j.base.util;

/**
 * Created by LinShunkang on 2019/11/03
 */
public final class NumUtils {

    public static double getPercent(double curNum, double maxNum) {
        if (curNum > 0L && maxNum > 0L) {
            return (100D * curNum) / maxNum;
        }
        return 0D;
    }

}
