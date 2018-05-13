package cn.myperf4j.aspectj;

import cn.myperf4j.core.*;
import cn.myperf4j.core.annotation.NonProfiler;
import cn.myperf4j.core.annotation.Profiler;
import cn.myperf4j.core.config.ProfilerParams;
import cn.myperf4j.core.config.ProfilingFilter;
import cn.myperf4j.core.util.ClassScanner;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.MapUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
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
            Set<String> packages = ProfilingFilter.getIncludePackage();
            for (String pkg : packages) {
                if (!processPackage(pkg)) {
                    Logger.warn("AspectJRecorderMaintainer.processPackage(" + pkg + "): FAILURE!!!");
                }
            }

            return true;
        } catch (Exception e) {
            Logger.error("AspectJRecorderMaintainer.initRecorderMap()", e);
        } finally {
            Logger.info("AspectJRecorderMaintainer.initRecorderMap() cost:" + (System.currentTimeMillis() - start) + "ms");
        }
        return false;
    }

    private boolean processPackage(String pkg) {
        try {
            Set<Class<?>> classSet = getClasses(pkg.replace('/', '.'));
            Iterator<Class<?>> iterator = classSet.iterator();
            while (iterator.hasNext()) {
                Class<?> clazz = iterator.next();
                if (ProfilingFilter.isNotNeedInject(clazz.getName())) {
                    iterator.remove();
                }
            }
            processAnnotations(classSet);

            return true;
        } catch (Exception e) {
            Logger.error("AspectJRecorderMaintainer.processPackage(" + pkg + ")", e);
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
                Logger.error("AspectJRecorderMaintainer.processAnnotations(classSet): " + throwable.getMessage());
            }
        }
        Logger.info("AspectJRecorderMaintainer.processAnnotations() cost:" + (System.currentTimeMillis() - startMills) + "ms");
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
