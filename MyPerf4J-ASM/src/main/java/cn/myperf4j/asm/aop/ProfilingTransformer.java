package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.aop.pkg.PackageClassAdapter;
import cn.myperf4j.asm.aop.profiler.ProfilerClassAdapter;
import cn.myperf4j.core.config.ProfilingConfig;
import cn.myperf4j.core.config.ProfilingFilter;
import cn.myperf4j.core.util.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilingTransformer implements ClassFileTransformer {

    private boolean profilingByProfiler = ProfilingConfig.getInstance().profilingByProfiler();

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

            Logger.info("ProfilingTransformer.transform(" + loader + ", " + className + ", classBeingRedefined, protectionDomain, " + classFileBuffer.length + ")...");
            ClassReader cr = new ClassReader(classFileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = getClassVisitor(cw, className);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);

            return cw.toByteArray();
        } catch (Throwable e) {
            Logger.error("ProfilingTransformer.transform(" + loader + ", " + className + ", " + classBeingRedefined + ", " + protectionDomain + ", " + classFileBuffer.length + ")", e);
        }
        return classFileBuffer;
    }

    private ClassVisitor getClassVisitor(ClassWriter cw, String className) {
        if (profilingByProfiler) {
            return new ProfilerClassAdapter(cw, className, false);
        } else {
            return new PackageClassAdapter(cw, className, false);
        }
    }
}
