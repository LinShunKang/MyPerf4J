package cn.myperf4j.base.config;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilingFilter {

    /**
     * 不需要注入的Package集合
     */
    private static Set<String> excludePackage = new HashSet<>();

    /**
     * 需要注入的Package集合
     */
    private static Set<String> includePackage = new HashSet<>();


    /**
     * 不需要注入的method集合
     */
    private static Set<String> excludeMethods = new HashSet<>();


    /**
     * 不注入的ClassLoader集合
     */
    private static Set<String> excludeClassLoader = new HashSet<>();

    static {
        // 默认不注入的package
        excludePackage.add("java/");
        excludePackage.add("javax/");
        excludePackage.add("sun/");
        excludePackage.add("com/sun/");
        excludePackage.add("org/");
        excludePackage.add("com/intellij/");

        // 不注入MyPerf4J本身
        excludePackage.add("cn/myperf4j/");


        //默认不注入的method
        excludeMethods.add("main");
        excludeMethods.add("getClass");//java.lang.Object
        excludeMethods.add("hashCode");//java.lang.Object
        excludeMethods.add("equals");//java.lang.Object
        excludeMethods.add("clone");//java.lang.Object
        excludeMethods.add("toString");//java.lang.Object
        excludeMethods.add("notify");//java.lang.Object
        excludeMethods.add("wait");//java.lang.Object
        excludeMethods.add("finalize");//java.lang.Object
        excludeMethods.add("afterPropertiesSet");//spring
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->不需要修改字节码  false->需要修改字节码
     */
    public static boolean isNotNeedInject(String innerClassName) {
        if (innerClassName == null) {
            return false;
        }

        if (innerClassName.indexOf('$') >= 0) {
            return true;
        }

        for (String prefix : excludePackage) {
            if (innerClassName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static void addExcludePackage(String pkg) {
        if (pkg == null || pkg.isEmpty()) {
            return;
        }

        excludePackage.add(pkg.replace('.', '/').trim());
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNeedInject(String innerClassName) {
        if (innerClassName == null) {
            return false;
        }

        for (String prefix : includePackage) {
            if (innerClassName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static void addIncludePackage(String pkg) {
        if (pkg == null || pkg.isEmpty()) {
            return;
        }

        includePackage.add(pkg.replace('.', '/').trim());
    }

    public static Set<String> getExcludePackage() {
        return new HashSet<>(excludePackage);
    }

    public static Set<String> getIncludePackage() {
        return new HashSet<>(includePackage);
    }


    /**
     * @param methodName
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNotNeedInjectMethod(String methodName) {
        if (methodName == null) {
            return false;
        }

        if (methodName.indexOf('$') >= 0) {
            return true;
        }

        return excludeMethods.contains(methodName);
    }

    public static void addExcludeMethods(String method) {
        if (method == null) {
            return;
        }

        excludeMethods.add(method.trim());
    }

    public static Set<String> getExcludeMethods() {
        return new HashSet<>(excludeMethods);
    }


    /**
     * @param classLoader
     */
    public static void addExcludeClassLoader(String classLoader) {
        excludeClassLoader.add(classLoader);
    }


    /**
     * 是否是不需要注入的类加载器
     *
     * @param classLoader
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNotNeedInjectClassLoader(String classLoader) {
        return excludeClassLoader.contains(classLoader);
    }
}
