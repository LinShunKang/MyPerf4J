package cn.myperf4j.aspectj;

import cn.myperf4j.core.*;
import cn.myperf4j.core.annotation.NonProfiler;
import cn.myperf4j.core.annotation.Profiler;
import cn.myperf4j.core.util.ClassScanner;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.MapUtils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by LinShunkang on 2018/4/25
 */
public class AspectJRecorderMaintainer extends AbstractRecorderMaintainer {

    private static final AspectJRecorderMaintainer instance = new AspectJRecorderMaintainer();

    private AspectJRecorderMaintainer() {
        //为了让recorderMap.get()更加快速，减小loadFactor->减少碰撞的概率->加快get()的执行速度
        recorderMap = MapUtils.createHashMap(10240, 0.2F);
        backupRecorderMap = MapUtils.createHashMap(10240, 0.2F);
    }

    public static AspectJRecorderMaintainer getInstance() {
        return instance;
    }

    @Override
    public boolean initRecorderMap() {
        long start = System.currentTimeMillis();
        try {


            URL enumeration = AspectJRecorderMaintainer.class.getClassLoader().getResource("");
            if (enumeration == null) {
                return false;
            }

            File file = new File(enumeration.getPath());
            if (!file.exists() || !file.isDirectory()) {
                return false;
            }

            File[] dirFiles = file.listFiles(new FileFilter() {
                // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().endsWith(".class");
                }
            });

            if (dirFiles == null || dirFiles.length <= 0) {
                return false;
            }

            for (int i = 0; i < dirFiles.length; ++i) {
                File f = dirFiles[i];
                processAnnotations(getClasses(f.getName()));
            }
            Logger.info("RecorderMaintainer.initRecorderMap() cost:" + (System.currentTimeMillis() - start) + "ms");
            return true;
        } catch (Exception e) {
            Logger.error("RecorderMaintainer.initRecorderMap()", e);
        }
        return false;
    }

    private Set<Class<?>> getClasses(String packageName) {
        Logger.info("Begin scanning " + packageName + "...");
        ClassScanner handler = new ClassScanner(true, true);
        return handler.getClasses(packageName, true);
    }

    private void processAnnotations(Set<Class<?>> classSet) {
        long startMills = System.currentTimeMillis();
        for (Class<?> clazz : classSet) {
            try {
                Profiler classProfiler = clazz.getAnnotation(Profiler.class);
                Method[] methodArray = clazz.getMethods();
                for (int k = 0, length = methodArray.length; k < length; ++k) {
                    Method method = methodArray[k];
                    if (!clazz.equals(method.getDeclaringClass()) || clazz.getName().startsWith("org.springframework")) {
                        continue;
                    }

                    NonProfiler methodNonProfiler = method.getAnnotation(NonProfiler.class);
                    if (methodNonProfiler != null) {
                        continue;
                    }

                    Profiler methodProfiler = method.getAnnotation(Profiler.class);
                    if (methodProfiler == null && (methodProfiler = classProfiler) == null) {
                        continue;
                    }

                    //从性能角度考虑，只用类名+方法名，不去组装方法的参数类型！！！
                    String api = clazz.getSimpleName() + "." + method.getName();
                    recorderMap.put(api, createRecorder(api, methodProfiler));
                    backupRecorderMap.put(api, createRecorder(api, methodProfiler));
                }
            } catch (Throwable throwable) {
                Logger.error("processAnnotations(classSet): " + throwable.getMessage());
            }
        }
        Logger.info("RecorderMaintainer.processAnnotations() cost:" + (System.currentTimeMillis() - startMills) + "ms");
    }

    @Override
    public void addRecorder(String tag, ProfilerParams profilerParams) {
        //empty
    }

    @Override
    public AbstractRecorder getRecorder(String api) {
        return recorderMap.get(api);
    }

    @Override
    public Map<String, AbstractRecorder> getRecorderMap() {
        return Collections.unmodifiableMap(recorderMap);
    }

    @Override
    public boolean initOther() {
        return true;
    }
}
