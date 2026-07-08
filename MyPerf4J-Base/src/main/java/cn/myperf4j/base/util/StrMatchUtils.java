package cn.myperf4j.base.util;

/**
 * Created by LinShunkang on 2019/05/04
 */
public final class StrMatchUtils {

    public static final char WILDCARD = '*';

    /**
     * 该方法用于判断 str 是否满足 exp 表达式
     * 注意：目前只处理 '*' 作为模糊匹配
     * <p>
     * 参考链接如下： <br/>
     * 1、<a href="https://www.cnblogs.com/daleyzou/p/9535134.html">通配符匹配</a> <br/>
     * 2、<a href="https://shmilyaw-hotmail-com.iteye.com/blog/2154716">Wildcard Matching</a>
     */
    public static boolean isMatch(String str, String exp) {
        return isMatch(str, 0, exp, 0);
    }

    public static boolean isMatch(String str, int strStartIdx, String exp, int expStartIdx) {
        final int strLen = str.length(), expLen = exp.length();
        int strIndex = strStartIdx, expIndex = expStartIdx, starIndex = -1; //starIndex 记录的是上一个 WILDCARD 的位置
        while (strIndex < strLen) {
            final char pkgChar = str.charAt(strIndex);
            final char expChar = expIndex < expLen ? exp.charAt(expIndex) : '\0';
            if (pkgChar == expChar) { //字符相等
                strIndex++;
                expIndex++;
            } else if (expChar == WILDCARD) { //遇到 WILDCARD, 记录 WILDCARD 的位置，并记录 expIndex 和 match
                starIndex = expIndex;
                expIndex++;
            } else if (starIndex != -1) { //不是上述两种情况，无法匹配，因此回溯
                expIndex = starIndex + 1;
                strIndex++;
            } else { //其他情况， 直接返回false
                return false;
            }
        }

        //检测 exp 尾部是否全部都为 WILDCARD
        while (expIndex < expLen && exp.charAt(expIndex) == WILDCARD) {
            expIndex++;
        }

        //若 exp 尾部全部为 WILDCARD，说明匹配
        return expIndex == expLen;
    }

    private StrMatchUtils() {
        //empty
    }
}
