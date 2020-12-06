package cn.myperf4j.asm.aop;

import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.util.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

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
        try {
            if (ProfilingFilter.isNotNeedInject(className)) {
                return classFileBuffer;
            }

            if (!ProfilingFilter.isNeedInject(className)) {
                return classFileBuffer;
            }

            if (loader != null && ProfilingFilter.isNotNeedInjectClassLoader(loader.getClass().getName())) {
                return classFileBuffer;
            }

            Logger.info("ProfilingTransformer.transform(" + getClassLoaderName(loader) + ", " + className
                    + ", classBeingRedefined, protectionDomain, " + classFileBuffer.length + ")...");
            return getBytes(loader, className, classFileBuffer);
        } catch (Throwable e) {
            Logger.error("ProfilingTransformer.transform(" + getClassLoaderName(loader) + ", " + className + ", "
                    + classBeingRedefined + ", protectionDomain, " + classFileBuffer.length + ")", e);
        }
        return classFileBuffer;
    }

    private byte[] getBytes(ClassLoader loader,
                            String className,
                            byte[] classFileBuffer) {
        if (needComputeMaxs(loader)) {
            ClassReader cr = new ClassReader(classFileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ProfilingClassAdapter(cw, className);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } else {
            ClassReader cr = new ClassReader(classFileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new ProfilingClassAdapter(cw, className);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        }
    }

    private boolean needComputeMaxs(ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }

        String loaderName = getClassLoaderName(classLoader);
        return loaderName.equals("org.apache.catalina.loader.WebappClassLoader")
                || loaderName.equals("org.apache.catalina.loader.ParallelWebappClassLoader")
                || loaderName.equals("org.springframework.boot.loader.LaunchedURLClassLoader")
                || loaderName.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders")
                ;
    }

    private String getClassLoaderName(ClassLoader classLoader) {
        if (classLoader == null) {
            return "null";
        }

        return classLoader.getClass().getName();
    }
}
