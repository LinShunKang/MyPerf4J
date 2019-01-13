package cn.myperf4j.base.util;

import java.util.Set;

/**
 * Created by LinShunkang on 2018-12-31
 * 该类用于解析 MyPerf4J 自定义的包路径表达式解析
 * 规则：
 * 1、可以用 [] 代表集合的概念，集合 [e1,e2,e3] 中包含多个元素，每个元素由英文逗号分隔，元素可以是 package 和 class。但 [] 不可嵌套出现。例如：
 * a、cn.myperf4j.util.[Logger,DateUtils] -> cn.myperf4j.util.Logger;cn.myperf4j.util.DateUtils
 * b、cn.myperf4j.metric.[formatter,processor] -> cn.myperf4j.metric.formatter;cn.myperf4j.metric.processor
 * c、cn.myperf4j.metric.[formatter,MethodMetrics] -> cn.myperf4j.metric.formatter;cn.myperf4j.metric.MethodMetrics
 * <p>
 * 2、可以用 * 代表贪心匹配，可以匹配多个字符。TODO
 */
public final class PkgExpUtils {

    public static Set<String> parse(String expStr) {
        int leftIdx = expStr.indexOf("[");
        if (leftIdx < 0) {
            return SetUtils.of(expStr);
        }

        int rightIdx = expStr.indexOf("]", leftIdx);
        if (rightIdx < 0) {
            throw new IllegalArgumentException("PkgExpUtils.parse(\"" + expStr + "\"): '[' always paired with ']'");
        }

        String prefixStr = expStr.substring(0, leftIdx);
        String suffixStr = rightIdx + 1 < expStr.length() ? expStr.substring(rightIdx + 1) : "";

        String elementsStr = expStr.substring(leftIdx + 1, rightIdx);
        String[] elements = elementsStr.split(",");
        Set<String> result = SetUtils.createHashSet(elements.length);
        for (int i = 0; i < elements.length; ++i) {
            String subExpStr = prefixStr.concat(elements[i]).concat(suffixStr);
            result.addAll(parse(subExpStr));
        }
        return result;
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
        int starIndex = -1;//记录上一个 '*' 的位置
        int match = 0;//记录与 '*' 匹配的 strIndex 的位置 (与 starIndex 不同的是，每次回溯，match自增)

        while (strIndex < str.length()) {
            char pkgChar = str.charAt(strIndex);
            char expChar = expIndex < exp.length() ? exp.charAt(expIndex) : '\0';
            if (pkgChar == expChar) {//字符相等
                strIndex++;
                expIndex++;
            } else if (expChar == '*') {//遇到'*', 记录'*'的位置，并记录 expIndex 和 match
                starIndex = expIndex;
                expIndex++;
                match = strIndex;
            } else if (starIndex != -1) {//不是上述两种情况，无法匹配，因此回溯
                expIndex = starIndex + 1;
                match++;
                strIndex = match;
            } else {//其他情况， 直接返回false
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

    public static void main(String[] args) {
        System.out.println(isMatch("cn.myperf4j.config.abc", "cn.myperf4j*abc"));
        System.out.println(isMatch("cn.myperf4j.config.abc", "*.myperf4j*abc"));
        System.out.println(isMatch("cn.myperf4j.config.abc", "*.myperf4j*a*c"));
    }
}
