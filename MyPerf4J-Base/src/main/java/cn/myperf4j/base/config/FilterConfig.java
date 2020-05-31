package cn.myperf4j.base.config;

import cn.myperf4j.base.util.StrUtils;

import static cn.myperf4j.base.config.MyProperties.getBoolean;
import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.constant.PropertyKeys.Filter.CLASS_LOADERS_EXCLUDE;
import static cn.myperf4j.base.constant.PropertyKeys.Filter.METHODS_EXCLUDE;
import static cn.myperf4j.base.constant.PropertyKeys.Filter.METHODS_EXCLUDE_PRIVATE;
import static cn.myperf4j.base.constant.PropertyKeys.Filter.PACKAGES_EXCLUDE;
import static cn.myperf4j.base.constant.PropertyKeys.Filter.PACKAGES_INCLUDE;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class FilterConfig {

    private String excludeClassLoaders;

    private String includePackages;

    private String excludePackages;

    private String excludeMethods;

    private boolean excludePrivateMethod;

    public String excludeClassLoaders() {
        return excludeClassLoaders;
    }

    public void excludeClassLoaders(String excludeClassLoaders) {
        this.excludeClassLoaders = excludeClassLoaders;
    }

    public String includePackages() {
        return includePackages;
    }

    public void includePackages(String includePackages) {
        this.includePackages = includePackages;
    }

    public String excludePackages() {
        return excludePackages;
    }

    public void excludePackages(String excludePackages) {
        this.excludePackages = excludePackages;
    }

    public String excludeMethods() {
        return excludeMethods;
    }

    public void excludeMethods(String excludeMethods) {
        this.excludeMethods = excludeMethods;
    }

    public boolean excludePrivateMethod() {
        return excludePrivateMethod;
    }

    public void excludePrivateMethod(boolean excludePrivateMethod) {
        this.excludePrivateMethod = excludePrivateMethod;
    }

    @Override
    public String toString() {
        return "FilterConfig{" +
                "excludeClassLoaders='" + excludeClassLoaders + '\'' +
                ", includePackages='" + includePackages + '\'' +
                ", excludePackages='" + excludePackages + '\'' +
                ", excludeMethods='" + excludeMethods + '\'' +
                ", excludePrivateMethod=" + excludePrivateMethod +
                '}';
    }

    public static FilterConfig loadFilterConfig() {
        String includePackages = getStr(PACKAGES_INCLUDE);
        if (StrUtils.isBlank(includePackages)) {
            throw new IllegalArgumentException(PACKAGES_INCLUDE.key() + " or " + PACKAGES_INCLUDE.legacyKey() +
                    " is required!!!");
        }

        FilterConfig config = new FilterConfig();
        config.includePackages(includePackages);
        config.excludeClassLoaders(getStr(CLASS_LOADERS_EXCLUDE));
        config.excludePackages(getStr(PACKAGES_EXCLUDE));
        config.excludeMethods(getStr(METHODS_EXCLUDE));
        config.excludePrivateMethod(getBoolean(METHODS_EXCLUDE_PRIVATE, true));
        return config;
    }
}
