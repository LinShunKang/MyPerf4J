package cn.myperf4j.base.config;

import cn.myperf4j.base.util.PkgExpUtils;
import cn.myperf4j.base.util.StrMatchUtils;
import cn.myperf4j.base.util.StrUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilingFilter {

    /**
     * 不需要注入的 Package前缀 集合
     */
    private static Set<String> excludePackagePrefix = new HashSet<>();

    /**
     * 不需要注入的 Package表达式 集合
     */
    private static Set<String> excludePackageExp = new HashSet<>();

    /**
     * 需要注入的 Package前缀 集合
     */
    private static Set<String> includePackagePrefix = new HashSet<>();

    /**
     * 需要注入的 Package表达式 集合
     */
    private static Set<String> includePackageExp = new HashSet<>();


    /**
     * 不需要注入的 method 集合
     */
    private static Set<String> excludeMethods = new HashSet<>();


    /**
     * 不注入的 ClassLoader 集合
     */
    private static Set<String> excludeClassLoader = new HashSet<>();

    static {
        // 默认不注入的 package
        excludePackagePrefix.add("java/");
        excludePackagePrefix.add("javax/");
        excludePackagePrefix.add("sun/");
        excludePackagePrefix.add("com/sun/");
        excludePackagePrefix.add("com/intellij/");

        // 不注入 MyPerf4J 本身
        excludePackagePrefix.add("cn/myperf4j/");

        // 默认注入的 package
        includePackagePrefix.add("net/paoding/rose/jade/context/JadeInvocationHandler");//Jade
        includePackagePrefix.add("org/apache/ibatis/binding/MapperProxy");//Mybatis
        includePackagePrefix.add("com/alibaba/dubbo/rpc/proxy/InvokerInvocationHandler");//DUBBO
        includePackagePrefix.add("org/apache/dubbo/rpc/proxy/InvokerInvocationHandler");//DUBBO
        includePackagePrefix.add("com/alipay/sofa/rpc/proxy/jdk/JDKInvocationHandler");//SOFA jdk-proxy
        includePackagePrefix.add("com/weibo/api/motan/proxy/RefererInvocationHandler");//Motan

        //默认不注入的method
        excludeMethods.add("main");
        excludeMethods.add("premain");
        excludeMethods.add("getClass");//java.lang.Object
        excludeMethods.add("hashCode");//java.lang.Object
        excludeMethods.add("equals");//java.lang.Object
        excludeMethods.add("clone");//java.lang.Object
        excludeMethods.add("toString");//java.lang.Object
        excludeMethods.add("notify");//java.lang.Object
        excludeMethods.add("notifyAll");//java.lang.Object
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

        return isMatch(innerClassName, excludePackagePrefix, excludePackageExp);
    }

    private static boolean isMatch(String innerClassName, Set<String> pkgPrefixSet, Set<String> pkgExpSet) {
        for (String prefix : pkgPrefixSet) {
            if (innerClassName.startsWith(prefix)) {
                return true;
            }
        }

        for (String exp : pkgExpSet) {
            if (StrMatchUtils.isMatch(innerClassName, exp)) {
                return true;
            }
        }
        return false;
    }

    public static void addExcludePackage(String pkg) {
        if (StrUtils.isEmpty(pkg)) {
            return;
        }

        addPackages(pkg, excludePackagePrefix, excludePackageExp);
    }

    private static void addPackages(String packages, Set<String> pkgPrefixSet, Set<String> pkgExpSet) {
        Set<String> pkgSet = PkgExpUtils.parse(packages);
        for (String pkg : pkgSet) {
            pkg = preprocess(pkg);
            if (pkg.indexOf('*') > 0) {
                pkgExpSet.add(pkg);
            } else {
                pkgPrefixSet.add(pkg);
            }
        }
    }

    private static String preprocess(String pkg) {
        return pkg.replace('.', '/').trim();
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNeedInject(String innerClassName) {
        if (innerClassName == null) {
            return false;
        }

        return isMatch(innerClassName, includePackagePrefix, includePackageExp);
    }

    public static void addIncludePackage(String pkg) {
        if (StrUtils.isEmpty(pkg)) {
            return;
        }

        addPackages(pkg, includePackagePrefix, includePackageExp);
    }

    public static Set<String> getExcludePackagePrefix() {
        return new HashSet<>(excludePackagePrefix);
    }

    public static Set<String> getIncludePackagePrefix() {
        return new HashSet<>(includePackagePrefix);
    }


    /**
     * @param methodName
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNotNeedInjectMethod(String methodName) {
        if(methodName == null) {
            return false;
        }

        int dotIndex = methodName.indexOf('.');
        int leftParenIndex = methodName.indexOf('(');

        if(dotIndex >= 0 && leftParenIndex >= 0) {
            methodName = methodName.substring(dotIndex + 1, leftParenIndex);
        }

        if(methodName.indexOf('$') >= 0) {
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
