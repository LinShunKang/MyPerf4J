package cn.myperf4j.core;

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
    private static Set<String> includePackage = new HashSet<>();

    static {
        // 默认不注入的Package
        excludePackage.add("java.");
        excludePackage.add("javax.");
        excludePackage.add("sun.");
        excludePackage.add("com.sun.");
        excludePackage.add("org.");
        excludePackage.add("com.intellij.");

        // 不注入profiler本身
        excludePackage.add("cn.myperf4j.");

    }

    /**
     * @param className : 形如: cn.myperf4j.core.ProfilerFilter
     * @return
     */
    public static boolean isNotNeedInject(String className) {
        String clazzName = className.replace('/', '.');
        for (String prefix : excludePackage) {
            if (clazzName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static void addNotNeedInjectPackage(String pkg) {
        excludePackage.add(pkg.replace('/', '.'));
    }

    /**
     * @param className : 形如: cn.myperf4j.core.ProfilerFilter
     * @return
     */
    public static boolean isNeedInject(String className) {
        String clazzName = className.replace('/', '.');
        for (String prefix : includePackage) {
            if (clazzName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static void addNeedInjectPackage(String pkg) {
        includePackage.add(pkg.replace('/', '.'));
    }

    public static Set<String> getExcludePackage() {
        return new HashSet<>(excludePackage);
    }

    public static Set<String> getIncludePackage() {
        return new HashSet<>(includePackage);
    }
}
