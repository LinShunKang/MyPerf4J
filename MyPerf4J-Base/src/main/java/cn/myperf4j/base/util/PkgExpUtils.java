package cn.myperf4j.base.util;

import cn.myperf4j.base.util.collections.SetUtils;

import java.util.List;
import java.util.Set;

import static cn.myperf4j.base.constant.PropertyValues.Separator.ARR_ELE;

/**
 * Created by LinShunkang on 2018-12-31
 * 该类用于解析 MyPerf4J 自定义的包路径表达式解析
 * 规则：
 * 1、可以用 [] 代表集合的概念，集合 [e1,e2,e3] 中包含多个元素，每个元素由英文逗号分隔，元素可以是 package 和 class。但 [] 不可嵌套出现。
 * 例如：
 * a、cn.myperf4j.util.[Logger,DateUtils] -> cn.myperf4j.util.Logger;cn.myperf4j.util.DateUtils
 * b、cn.myperf4j.metric.[formatter,processor] -> cn.myperf4j.metric.formatter;cn.myperf4j.metric.processor
 * c、cn.myperf4j.metric.[formatter,MethodMetrics] -> cn.myperf4j.metric.formatter;cn.myperf4j.metric.MethodMetrics
 * <p>
 * 2、可以用 * 代表贪心匹配，可以匹配多个字符。
 */
public final class PkgExpUtils {

    private PkgExpUtils() {
        //empty
    }

    public static Set<String> parse(String expStr) {
        final int leftIdx = expStr.indexOf('[');
        if (leftIdx < 0) {
            return SetUtils.of(expStr);
        }

        final int rightIdx = expStr.indexOf(']', leftIdx);
        if (rightIdx < 0) {
            throw new IllegalArgumentException("PkgExpUtils.parse(\"" + expStr + "\"): '[' always paired with ']'");
        }

        final String prefixStr = expStr.substring(0, leftIdx);
        final String suffixStr = rightIdx + 1 < expStr.length() ? expStr.substring(rightIdx + 1) : "";
        final String elementsStr = expStr.substring(leftIdx + 1, rightIdx);
        final List<String> elements = StrUtils.splitAsList(elementsStr, ARR_ELE);
        final Set<String> result = SetUtils.createHashSet(elements.size() << 2);
        for (String element : elements) {
            result.addAll(parse(prefixStr.concat(element).concat(suffixStr)));
        }
        return result;
    }
}
