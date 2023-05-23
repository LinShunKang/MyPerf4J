package cn.myperf4j.asm.aop;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilingTransformer implements ClassFileTransformer {

    private final File debuggingClassDir;

    public ProfilingTransformer() {
        //TODO:LSK 提取一个工具类，把存储 debug class 的逻辑放到里面
        this.debuggingClassDir = new File(ProfilingConfig.basicConfig().debugClassDir());
        final boolean mkdirs = debuggingClassDir.mkdirs();
        Logger.info("debuggingClassesDir=" + debuggingClassDir.getAbsolutePath() + ", mkdirs()=" + mkdirs);
    }

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

            final byte[] bytes = getBytes(loader, className, classFileBuffer);
            if (ProfilingConfig.basicConfig().debug()) {
                FileUtils.writeToFile(bytes, new File(debuggingClassDir, className.replace('/', '.') + ".class"));
            }
            return bytes;
        } catch (Throwable t) {
            Logger.error("ProfilingTransformer.transform(" + getClassLoaderName(loader) + ", " + className + ", "
                    + classBeingRedefined + ", protectionDomain, " + classFileBuffer.length + ")", t);
        }
        return classFileBuffer;
    }

    private byte[] getBytes(final ClassLoader loader,
                            final String className,
                            final byte[] classFileBuffer) {
        final ClassReader cr = new ClassReader(classFileBuffer);
        final ClassWriter cw = new ClassWriter(cr, needComputeMaxs(loader) ? COMPUTE_MAXS : COMPUTE_FRAMES) {
            @Override
            protected ClassLoader getClassLoader() {
                return loader != null ? loader : super.getClassLoader();
            }
        };
        final ClassVisitor cv = new ProfilingClassAdapter(cw, className);
        cr.accept(cv, EXPAND_FRAMES);
        return cw.toByteArray();
    }

    private boolean needComputeMaxs(ClassLoader classLoader) {
        final String loaderName = getClassLoaderName(classLoader);
        return loaderName.equals("org.apache.catalina.loader.WebappClassLoader")
                || loaderName.equals("org.apache.catalina.loader.ParallelWebappClassLoader")
                || loaderName.equals("org.springframework.boot.loader.LaunchedURLClassLoader")
                || loaderName.startsWith("org.apache.flink.runtime.execution.librarycache.FlinkUserCodeClassLoaders")
                ;
    }

    private String getClassLoaderName(ClassLoader classLoader) {
        return classLoader == null ? "null" : classLoader.getClass().getName();
    }
}
