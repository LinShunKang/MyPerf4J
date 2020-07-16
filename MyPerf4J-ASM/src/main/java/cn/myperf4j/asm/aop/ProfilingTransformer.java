package cn.myperf4j.asm.aop;

import cn.myperf4j.base.config.ProfilingConfig;
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

            Logger.info("ProfilingTransformer.transform(" + getClassLoaderName(loader) + ", " + className + ", classBeingRedefined, protectionDomain, " + classFileBuffer.length + ")...");
            return getBytes(loader, className, classFileBuffer);
        } catch (Throwable e) {
            Logger.error("ProfilingTransformer.transform(" + getClassLoaderName(loader) + ", " + className + ", " + classBeingRedefined + ", protectionDomain, " + classFileBuffer.length + ")", e);
        }
        return classFileBuffer;
    }

    private byte[] getBytes(ClassLoader loader,
                            String className,
                            byte[] classFileBuffer) {
        ClassReader cr = new ClassReader(classFileBuffer);
        ClassWriter cw = new ClassWriter(cr, getComputeMode(loader));
        ClassVisitor cv = new ProfilingClassAdapter(cw, className);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    private int getComputeMode(ClassLoader classLoader) {

        Integer computeMode = ProfilingConfig.basicConfig().asmComputeMode();

        if(computeMode != null) {
            return computeMode;
        }

        if (classLoader != null) {

            String loaderName = getClassLoaderName(classLoader);

            if(loaderName.equals("org.apache.catalina.loader.WebappClassLoader")
                    || loaderName.equals("org.apache.catalina.loader.ParallelWebappClassLoader")
                    || loaderName.equals("org.springframework.boot.loader.LaunchedURLClassLoader")) {
                return ClassWriter.COMPUTE_MAXS;
            }
        }
        return ClassWriter.COMPUTE_FRAMES;
    }

    private String getClassLoaderName(ClassLoader classLoader) {
        if (classLoader == null) {
            return "null";
        }

        return classLoader.getClass().getName();
    }
}
