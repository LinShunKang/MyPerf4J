package cn.myperf4j.asm.aop;

import cn.myperf4j.base.util.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static cn.myperf4j.base.config.ProfilingFilter.isNeedInject;
import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInject;
import static cn.myperf4j.base.config.ProfilingFilter.isNotNeedInjectClassLoader;
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilingTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFileBuffer) {
        final String classLoaderName = getClassLoaderName(loader);
        try {
            if (isNotNeedInject(className) || !isNeedInject(className)) {
                return classFileBuffer;
            }

            if (loader != null && isNotNeedInjectClassLoader(classLoaderName)) {
                return classFileBuffer;
            }

            Logger.info("ProfilingTransformer.transform(" + classLoaderName + ", " + className
                    + ", classBeingRedefined, protectionDomain, " + classFileBuffer.length + ")...");
            return getBytes(loader, className, classFileBuffer);
        } catch (Throwable t) {
            Logger.error("ProfilingTransformer.transform(" + classLoaderName + ", " + className + ", "
                    + classBeingRedefined + ", protectionDomain, " + classFileBuffer.length + ")", t);
        }
        return classFileBuffer;
    }

    private String getClassLoaderName(ClassLoader classLoader) {
        return classLoader == null ? "null" : classLoader.getClass().getName();
    }

    private byte[] getBytes(ClassLoader loader, String className, byte[] classFileBuffer) {
        final ClassReader cr = new ClassReader(classFileBuffer);
        final ClassWriter cw = new ClassWriter(cr, computeMax(loader) ? COMPUTE_MAXS : COMPUTE_FRAMES);
        cr.accept(new ProfilingClassAdapter(cw, className), EXPAND_FRAMES);
        return cw.toByteArray();
    }

    private boolean computeMax(ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }

        final String loaderName = classLoader.getClass().getName();
        return loaderName.equals("org.apache.catalina.loader.WebappClassLoader")
                || loaderName.equals("org.apache.catalina.loader.ParallelWebappClassLoader")
                || loaderName.equals("org.springframework.boot.loader.LaunchedURLClassLoader")
                || loaderName.equals("org.springframework.boot.loader.launch.LaunchedClassLoader")
                || loaderName.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders")
                ;
    }
}
