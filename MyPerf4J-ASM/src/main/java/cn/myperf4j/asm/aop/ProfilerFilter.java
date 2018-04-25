package cn.myperf4j.asm.aop;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilerFilter {

    /**
     * 不需要注入的Package集合
     */
    private static Set<String> excludePackage = new HashSet<>();

    /**
     * 需要注入的Package集合
     */
    protected static Set<String> includePackage = new HashSet<String>();

    static {
        // 默认不注入的Package
        excludePackage.add("java/");// 包含javax
        excludePackage.add("sun/");// 包含sunw
        excludePackage.add("com/sun/");
        excludePackage.add("org/");
        excludePackage.add("com/intellij");

        // 不注入profiler本身
        excludePackage.add("cn/myperf4j");


        includePackage.add("cn/perf4j");//TODO: DEL
    }

    public static boolean isNotNeedInject(String className) {
        String clazzName = className.toLowerCase().replace('.', '/');
        for (String prefix : excludePackage) {
            if (clazzName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNeedInject(String className) {
        String clazzName = className.toLowerCase().replace('.', '/');
        for (String v : includePackage) {
            if (clazzName.startsWith(v)) {
                return true;
            }
        }
        return false;
    }
}
