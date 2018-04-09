package cn.perf4j.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * 该类参考以下博客，并针对MyPerf4J做了一些修改：
 * http://sjsky.iteye.com/blog/1061092
 */
public final class ClassScanner {

    private final boolean excludeInner;//是否排除内部类 true->是 false->否

    private final boolean excludeJar;//是否排除jar包中的类 true->是 false->否

    private final boolean checkInOrEx;//过滤规则适用情况 true—>搜索符合规则的 false->排除符合规则的

    private final List<String> classFilters;//过滤规则列表 如果是null或者空，即全部符合不过滤

    public ClassScanner() {
        this(true, true, true, null);
    }

    public ClassScanner(boolean excludeInner, boolean excludeJar) {
        this(excludeInner, true, excludeJar, null);
    }

    public ClassScanner(boolean excludeInner,
                        boolean checkInOrEx,
                        boolean excludeJar,
                        List<String> classFilters) {
        this.excludeInner = excludeInner;
        this.checkInOrEx = checkInOrEx;
        this.excludeJar = excludeJar;
        this.classFilters = classFilters;
    }

    public Set<Class<?>> getClasses(String basePackage, boolean recursive) {
        Set<Class<?>> classes = new LinkedHashSet<>(1024);
        String packageName = basePackage;
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        }

        String package2Path = packageName.replace('.', '/');
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(package2Path);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    Logger.info("ClassScanner scanning file type class....");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    scanByFile(classes, packageName, filePath, recursive);
                } else if (!excludeJar && "jar".equals(protocol)) {
                    Logger.info("ClassScanner scanning jar type class....");
                    scanByJar(packageName, url, recursive, classes);
                }
            }
        } catch (IOException e) {
            Logger.error("ClassScanner.getClasses(" + basePackage + ", " + recursive + ")", e);
        }
        return classes;
    }

    private void scanByJar(String basePackage,
                           URL url,
                           boolean recursive,
                           Set<Class<?>> classes) {
        try {
            String package2Path = basePackage.replace('.', '/');
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(package2Path) || entry.isDirectory()) {
                    continue;
                }

                // 判断是否递归搜索子包
                if (!recursive && name.lastIndexOf('/') != package2Path.length()) {
                    continue;
                }

                // 判断是否过滤 inner class
                if (excludeInner && name.contains("$")) {
                    Logger.info("ClassScanner exclude inner class with name:" + name);
                    continue;
                }

                String classSimpleName = name.substring(name.lastIndexOf('/') + 1);
                // 判定是否符合过滤条件
                if (filterClassName(classSimpleName)) {
                    String className = name.replace('/', '.');
                    className = className.substring(0, className.length() - 6);
                    try {
                        classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                    } catch (Throwable e) {
                        Logger.error("ClassScanner loadClass(" + className + "): " + e.getCause());
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("ClassScanner.scanByJar(" + basePackage + ", " + url + ", " + recursive + ", classes)", e);
        }
    }

    private void scanByFile(Set<Class<?>> classes,
                            String packageName,
                            String packagePath,
                            final boolean recursive) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirFiles = dir.listFiles(new FileFilter() {
            // 自定义文件过滤规则
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return recursive;
                }

                String filename = file.getName();
                if (excludeInner && filename.contains("$")) {
                    Logger.info("ClassScanner exclude inner class with name:" + filename);
                    return false;
                }
                return filterClassName(filename);
            }
        });

        if (dirFiles == null) {
            return;
        }

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                scanByFile(classes, packageName + "." + file.getName(), file.getAbsolutePath(), recursive);
                continue;
            }

            String className = file.getName().substring(0, file.getName().length() - 6);
            try {
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
            } catch (Throwable e) {
                Logger.error("ClassScanner.scanByFile(classes, " + packageName + ", " + packagePath + ", " + recursive + "): loadClass(" + className + "）" + e.getCause());
            }
        }
    }

    private boolean filterClassName(String className) {
        if (!className.endsWith(".class")) {
            return false;
        }

        if (null == classFilters || classFilters.isEmpty()) {
            return true;
        }

        String tmpName = className.substring(0, className.length() - 6);
        boolean flag = false;
        for (String str : classFilters) {
            String tmpReg = "^" + str.replace("*", ".*") + "$";
            Pattern p = Pattern.compile(tmpReg);
            if (p.matcher(tmpName).find()) {
                flag = true;
                break;
            }
        }
        return (checkInOrEx && flag) || (!checkInOrEx && !flag);
    }

    public static void main(String[] args) {
        ClassScanner handler = new ClassScanner();
        Set<Class<?>> classSet = handler.getClasses("org", true);
        for (Class<?> cla : classSet) {
            System.out.println(cla.getName());
        }
    }
}