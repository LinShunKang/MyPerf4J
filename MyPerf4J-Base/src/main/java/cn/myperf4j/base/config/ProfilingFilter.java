package cn.myperf4j.base.config;

import cn.myperf4j.base.match.PackageMatcher;
import cn.myperf4j.base.util.PkgExpUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LinShunkang on 2018/4/24
 */
public final class ProfilingFilter {

    private static final List<String> BUILT_IN_INCLUDE_PACKAGES = Arrays.asList(
            "net/paoding/rose/jade/context/JadeInvocationHandler", //Jade
            "org/apache/ibatis/binding/MapperProxy", //Mybatis
            "com/alibaba/dubbo/rpc/proxy/InvokerInvocationHandler", //DUBBO
            "org/apache/dubbo/rpc/proxy/InvokerInvocationHandler", //DUBBO
            "com/alipay/sofa/rpc/proxy/jdk/JDKInvocationHandler", //SOFA
            "com/weibo/api/motan/proxy/RefererInvocationHandler" //Motan
    );

    private static final List<String> BUILT_IN_EXCLUDE_PACKAGES = Arrays.asList(
            "java/", "javax/", "sun/", "com/sun/", "com/intellij/", "cn/myperf4j/"
    );

    private static final PackageMatcher INCLUDE_PACKAGE_MATCHER = new PackageMatcher();

    private static final PackageMatcher EXCLUDE_PACKAGE_MATCHER = new PackageMatcher();

    private static final PackageMatcher ANNOTATION_SCAN_PACKAGE_MATCHER = new PackageMatcher();

    /**
     * 不需要注入的 method 集合
     */
    private static final Set<String> EXCLUDE_METHODS = new HashSet<>();

    /**
     * 不需要注入的 ClassLoader 集合
     */
    private static final Set<String> EXCLUDE_CLASS_LOADERS = new HashSet<>();

    /**
     * 需要注入的 Annotation 集合
     */
    private static final Set<String> INCLUDE_ANNOTATIONS = new HashSet<>();

    static {
        //默认不注入的method
        EXCLUDE_METHODS.add("main");
        EXCLUDE_METHODS.add("premain");
        EXCLUDE_METHODS.add("getClass"); //java.lang.Object
        EXCLUDE_METHODS.add("hashCode"); //java.lang.Object
        EXCLUDE_METHODS.add("equals"); //java.lang.Object
        EXCLUDE_METHODS.add("clone"); //java.lang.Object
        EXCLUDE_METHODS.add("toString"); //java.lang.Object
        EXCLUDE_METHODS.add("notify"); //java.lang.Object
        EXCLUDE_METHODS.add("notifyAll"); //java.lang.Object
        EXCLUDE_METHODS.add("wait"); //java.lang.Object
        EXCLUDE_METHODS.add("finalize"); //java.lang.Object
        EXCLUDE_METHODS.add("afterPropertiesSet"); //spring
    }

    private ProfilingFilter() {
        //empty
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->不需要修改字节码  false->需要修改字节码
     */
    public static boolean isNotNeedInject(String innerClassName) {
        if (innerClassName == null || innerClassName.indexOf('$') >= 0) {
            return true;
        }
        return EXCLUDE_PACKAGE_MATCHER.isMatch(innerClassName);
    }

    public static void addAllExcludePackage(List<String> pkgExprList) {
        final Set<String> packages = parsePackageExpr(pkgExprList);
        packages.addAll(BUILT_IN_EXCLUDE_PACKAGES);
        EXCLUDE_PACKAGE_MATCHER.init(packages);
    }

    private static Set<String> parsePackageExpr(List<String> pkgExprList) {
        final Set<String> packages = new HashSet<>(pkgExprList.size() * 2);
        for (String pkgExpr : pkgExprList) {
            for (String pkg : PkgExpUtils.parse(pkgExpr)) {
                packages.add(preprocess(pkg));
            }
        }
        return packages;
    }

    private static String preprocess(String pkg) {
        return pkg.replace('.', '/').trim();
    }

    /**
     * @param innerClassName : 形如: cn/myperf4j/core/ProfilingFilter
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNeedInject(String innerClassName) {
        return innerClassName != null && INCLUDE_PACKAGE_MATCHER.isMatch(innerClassName);
    }

    public static void addAllIncludePackage(List<String> pkgExprList) {
        final Set<String> packages = parsePackageExpr(pkgExprList);
        packages.addAll(BUILT_IN_INCLUDE_PACKAGES);
        INCLUDE_PACKAGE_MATCHER.init(packages);
    }

    /**
     * 是否是不需要执行代码注入的方法
     *
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNotNeedInjectMethod(String methodName) {
        if (methodName == null) {
            return false;
        }

        if (isSpecialMethod(methodName)) {
            return true;
        }
        return EXCLUDE_METHODS.contains(methodName);
    }

    private static boolean isSpecialMethod(String methodName) {
        final int symbolIndex = methodName.indexOf('$');
        if (symbolIndex < 0) {
            return false;
        }

        final int leftParenIndex = methodName.indexOf('(');
        return leftParenIndex < 0 || symbolIndex < leftParenIndex;
    }

    public static void addExcludeMethods(String method) {
        if (method != null) {
            EXCLUDE_METHODS.add(method.trim());
        }
    }

    public static void addExcludeClassLoader(String classLoader) {
        EXCLUDE_CLASS_LOADERS.add(classLoader);
    }

    /**
     * 是否是不需要执行代码注入的类加载器
     *
     * @return : true->不需要修改字节码  false->需要修改字节码
     */
    public static boolean isNotNeedInjectClassLoader(String classLoader) {
        return EXCLUDE_CLASS_LOADERS.contains(classLoader);
    }

    /**
     * 是否需要扫描 innerClassName 中包含指定注解
     */
    public static boolean isNeedScanAnnotation(String innerClassName) {
        return innerClassName != null && ANNOTATION_SCAN_PACKAGE_MATCHER.isMatch(innerClassName);
    }

    public static void addAllAnnotationScanPackages(List<String> pkgExprList) {
        ANNOTATION_SCAN_PACKAGE_MATCHER.init(parsePackageExpr(pkgExprList));
    }

    public static void addIncludeAnnotation(String annotationClassName) {
        INCLUDE_ANNOTATIONS.add("L" + annotationClassName.replace('.', '/') + ";");
    }

    /**
     * 是否是需要执行代码注入的注解
     *
     * @return : true->需要修改字节码  false->不需要修改字节码
     */
    public static boolean isNeedInjectAnnotation(String annotationDesc) {
        return INCLUDE_ANNOTATIONS.contains(annotationDesc);
    }
}
