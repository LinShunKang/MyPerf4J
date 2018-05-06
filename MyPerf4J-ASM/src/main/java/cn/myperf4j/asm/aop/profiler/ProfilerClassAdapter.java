package cn.myperf4j.asm.aop.profiler;

import cn.myperf4j.core.ProfilerParams;
import cn.myperf4j.core.annotation.Profiler;
import cn.myperf4j.core.util.Logger;
import org.objectweb.asm.*;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class ProfilerClassAdapter extends ClassVisitor implements Opcodes {

    private static final String PROFILER_INNER_NAME = Type.getDescriptor(Profiler.class);

    private static final ProfilerAntVisitor DEFAULT_AV = new ProfilerAntVisitor(ASM5, null, null);

    private ProfilerAntVisitor profilerAV = DEFAULT_AV;

    private String targetClassName;

    /**
     * 是否把方法体包进try-finally块；也就是当方法内抛异常的时候是否还记录响应时间；
     */
    private boolean addTryCatch;

    private boolean isInterface;

    public ProfilerClassAdapter(final ClassVisitor cv, String targetClassName, boolean addTryCatch) {
        super(ASM5, cv);
        int idx = targetClassName.replace('/', '.').lastIndexOf('.');
        this.targetClassName = targetClassName.substring(idx + 1, targetClassName.length());
        this.addTryCatch = addTryCatch;
    }

    public ProfilerClassAdapter(final ClassVisitor cv, String targetClassName) {
        this(cv, targetClassName, false);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Logger.debug("ProfilerClassAdapter.visit(" + version + ", " + access + ", " + name + ", " + signature + ", " + superName + ", " + Arrays.toString(interfaces) + ")");

        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        Logger.debug("ProfilerClassAdapter.visitMethod(" + access + ", " + name + ", " + desc + ", " + signature + ", " + Arrays.toString(exceptions) + ")");

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (isInterface || mv == null || "<init>".equals(name) || "<clinit>".equals(name)) {
            return mv;
        }

        if (addTryCatch) {
            return new ProfilerTryCatchMethodVisitor(access, name, desc, mv, targetClassName, profilerAV.getProfilerParams());
        } else {
            return new ProfilerSimpleMethodVisitor(access, name, desc, mv, targetClassName, profilerAV.getProfilerParams());
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Logger.debug("ProfilerClassAdapter.visitAnnotation(" + desc + ", " + visible + ")");

        AnnotationVisitor av = super.visitAnnotation(desc, visible);
        if (PROFILER_INNER_NAME.equals(desc) && av != null) {
            return profilerAV = new ProfilerAntVisitor(ASM5, av, ProfilerParams.of(true));
        }
        return av;
    }
}
