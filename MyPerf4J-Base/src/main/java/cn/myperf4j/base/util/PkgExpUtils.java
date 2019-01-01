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

}
