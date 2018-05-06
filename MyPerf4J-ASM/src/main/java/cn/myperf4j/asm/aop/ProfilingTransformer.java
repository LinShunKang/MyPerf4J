package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.aop.any.PackageClassAdapter;
import cn.myperf4j.asm.aop.profiler.ProfilerClassAdapter;
import cn.myperf4j.core.ProfilerFilter;
import cn.myperf4j.core.constant.PropertyKeys;
import cn.myperf4j.core.constant.PropertyValues;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.MyProperties;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilingTransformer implements ClassFileTransformer {

    private boolean profilingByProfiler = MyProperties.isSame(PropertyKeys.ASM_PROFILING_TYPE, PropertyValues.ASM_PROFILING_TYPE_PROFILER);

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFileBuffer) {
        try {
            if (ProfilerFilter.isNotNeedInject(className)) {
                Logger.debug("ProfilingTransformer.transform(" + loader + ", " + className + ", classBeingRedefined, protectionDomain, " + classFileBuffer.length + ") skip!!!");
                return classFileBuffer;
            }

            if (!ProfilerFilter.isNeedInject(className)) {
                Logger.debug("ProfilingTransformer.transform(" + loader + ", " + className + ", classBeingRedefined, protectionDomain, " + classFileBuffer.length + ") skip!!!");
                return classFileBuffer;
            }

            Logger.info("ProfilingTransformer.transform(" + loader + ", " + className + ", classBeingRedefined, protectionDomain, " + classFileBuffer.length + ")...");
            ClassReader cr = new ClassReader(classFileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
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
            return new ProfilerClassAdapter(cw, className, true);
        } else {
            return new PackageClassAdapter(cw, className, true);
        }
    }
}
