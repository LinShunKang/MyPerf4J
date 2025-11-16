package cn.myperf4j.base.util;

/**
 * Created by LinShunkang on 2019/05/04
 */
public final class StrMatchUtils {

    private StrMatchUtils() {
        //empty
    }

    /**
     * 该方法用于判断 str 是否满足 exp 表达式
     * 注意：目前只处理 '*' 作为模糊匹配
     * <p>
     * 参考链接如下：
     * 1、https://www.cnblogs.com/daleyzou/p/9535134.html
     * 2、https://shmilyaw-hotmail-com.iteye.com/blog/2154716
     */
    public static boolean isMatch(String str, String exp) {
        int strIndex = 0;
        int expIndex = 0;
        int starIndex = -1; //记录上一个 '*' 的位置
        while (strIndex < str.length()) {
            final char pkgChar = str.charAt(strIndex);
            final char expChar = expIndex < exp.length() ? exp.charAt(expIndex) : '\0';
            if (pkgChar == expChar) { //字符相等
                strIndex++;
                expIndex++;
            } else if (expChar == '*') { //遇到'*', 记录'*'的位置，并记录 expIndex 和 match
                starIndex = expIndex;
                expIndex++;
            } else if (starIndex != -1) { //不是上述两种情况，无法匹配，因此回溯
                expIndex = starIndex + 1;
                strIndex++;
            } else { //其他情况， 直接返回false
                return false;
            }
        }

        //检测 exp 尾部是否全部都为 '*'
        while (expIndex < exp.length() && exp.charAt(expIndex) == '*') {
            expIndex++;
        }

        //若 exp 尾部全部为 '*'，说明匹配
        return expIndex == exp.length();
    }
}
