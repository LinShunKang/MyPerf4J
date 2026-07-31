package cn.myperf4j.asm.aop;

import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.TypeDescUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

import static cn.myperf4j.base.config.ProfilingFilter.isNeedInjectAnnotation;

/**
 * Created by LinShunkang on 2026/07/05
 */
public class AnnotationProfilingClassAdapter extends AbstractClassAdapter {

    private boolean classAnnotated;

    public AnnotationProfilingClassAdapter(ClassVisitor cv, String innerClassName) {
        super(cv, innerClassName);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (isNeedInjectAnnotation(descriptor)) {
            classAnnotated = true;
        }
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        if (isInterface || !classAnnotated || !isNeedVisit(access, name)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        final String classMethodName = simpleClassName + "." + name;
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        final String desc4Human = TypeDescUtils.getMethodParamsDesc(desc);
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName + desc4Human)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null) {
            return null;
        }

        Logger.debug("AnnotationProfilingClassAdapter.visitMethod(" + access + ", " + name + ", " + desc + ", "
                + signature + ", " + Arrays.toString(exceptions) + "), innerClassName=" + innerClassName);

        return new ProfilingMethodVisitor(access, name, desc, mv, innerClassName, fullClassName, simpleClassName,
                classLevel, desc4Human);
    }
}
