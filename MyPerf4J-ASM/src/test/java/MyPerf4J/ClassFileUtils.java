package MyPerf4J;

import cn.myperf4j.base.util.Logger;
import cn.myperf4j.core.recorder.RoughRecorder;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class ClassFileUtils {

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(getClassFileContent(RoughRecorder.class.getName())));
    }

    public static byte[] getClassFileContent(String fullClassName) throws IOException {
        int idx = fullClassName.lastIndexOf(".");
        String simpleClassName = fullClassName.substring(idx + 1);
        File targetClassFile = getClasses(fullClassName.substring(0, idx), true, simpleClassName + ".class");
        if (targetClassFile == null) {
            return null;
        }

        return toByteArray(new FileInputStream(targetClassFile));
    }

    private static File getClasses(String basePackage, boolean recursive, String targetClassName) {
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
                    Logger.debug("ClassScanner scanning file type class....");
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    File file = scanByFile(packageName, filePath, recursive, targetClassName);
                    if (file != null) {
                        return file;
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("ClassScanner.getClasses(" + basePackage + ", " + recursive + ")", e);
        }
        return null;
    }

    private static File scanByFile(String packageName,
                                   String packagePath,
                                   final boolean recursive,
                                   String targetClassName) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        File[] dirFiles = dir.listFiles(new FileFilter() {
            // 自定义文件过滤规则
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return recursive;
                }
                return filterClassName(file.getName());
            }
        });

        if (dirFiles == null) {
            return null;
        }

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                scanByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, targetClassName);
                continue;
            }

            if (file.getName().equals(targetClassName)) {
                return file;
            }
        }
        return null;
    }

    private static boolean filterClassName(String className) {
        return className.endsWith(".class");
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }


}
