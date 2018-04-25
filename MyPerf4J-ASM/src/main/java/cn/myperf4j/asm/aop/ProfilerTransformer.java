package cn.myperf4j.asm.aop;

import cn.myperf4j.core.util.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * Created by LinShunkang on 2018/4/24
 */
public class ProfilerTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classFileBuffer) throws IllegalClassFormatException {
        try {
            if (ProfilerFilter.isNotNeedInject(className)) {
                Logger.warn("ProfilerTransformer.transform(" + loader + ", " + className + ",classBeingRedefined, protectionDomain, " + classFileBuffer.length + ") skip!!!");
                return classFileBuffer;
            }

            if (!ProfilerFilter.isNeedInject(className)) {
                Logger.warn("ProfilerTransformer.transform(" + loader + ", " + className + ",classBeingRedefined, protectionDomain, " + classFileBuffer.length + ") skip!!!");
                return classFileBuffer;
            }



            Logger.info("ProfilerTransformer.transform(" + loader + ", " + className + ",classBeingRedefined, protectionDomain, " + classFileBuffer.length + ") begin...");
            ClassReader cr = new ClassReader(classFileBuffer);

            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            MyClassAdapter cv = new MyClassAdapter(cw, className, true);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);

            return cw.toByteArray();
        } catch (Exception e) {
            Logger.error("ProfilerTransformer.transform(" + loader + ", " + className + ", " + classBeingRedefined + ", " + protectionDomain + ", " + classFileBuffer.length + ")", e);
        }
        return classFileBuffer;
    }
}
