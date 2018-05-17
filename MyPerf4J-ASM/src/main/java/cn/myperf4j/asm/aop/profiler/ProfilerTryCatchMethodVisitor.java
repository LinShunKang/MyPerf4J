package cn.myperf4j.asm.aop.profiler;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.asm.aop.ProfilingAspect;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.config.ProfilerParams;
import cn.myperf4j.core.annotation.NonProfiler;
import cn.myperf4j.core.annotation.Profiler;
import cn.myperf4j.core.util.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class ProfilerTryCatchMethodVisitor extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private static final String PROFILER_INNER_NAME = Type.getDescriptor(Profiler.class);

    private static final String NON_PROFILER_INNER_NAME = Type.getDescriptor(NonProfiler.class);

    private static final ProfilerAntVisitor DEFAULT_AV = new ProfilerAntVisitor(ASM5, null, null);

    private ProfilerAntVisitor profilerAV = DEFAULT_AV;

    private AbstractRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();

    private boolean hasProfiler = false;

    private boolean hasNonProfiler = false;

    private ProfilerParams classProfilerParams;

    private String tag;

    private int startTimeIdentifier;

    private Label startFinally = new Label();


    public ProfilerTryCatchMethodVisitor(int access,
                                         String name,
                                         String desc,
                                         MethodVisitor mv,
                                         String className,
                                         ProfilerParams classProfilerParams) {
        super(ASM5, mv, access, name, desc);
        this.tag = className + "." + name;
        this.classProfilerParams = classProfilerParams;
        this.hasProfiler = classProfilerParams.hasProfiler();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        Logger.debug("ProfilerTryCatchMethodVisitor.visitAnnotation(" + desc + ", " + visible + "): tag=" + tag);

        AnnotationVisitor av = super.visitAnnotation(desc, visible);
        if (av == null) {
            return null;
        }

        if (PROFILER_INNER_NAME.equals(desc)) {
            hasProfiler = true;
        }

        if (NON_PROFILER_INNER_NAME.equals(desc)) {
            hasNonProfiler = true;
        }

        if (profiling()) {
            return profilerAV = new ProfilerAntVisitor(ASM5, av, classProfilerParams);
        }
        return av;
    }

    private boolean profiling() {
        return hasProfiler && !hasNonProfiler;
    }

    /**
     * 此方法在访问方法的头部时被访问到，仅被访问一次
     */
    @Override
    public void visitCode() {
        Logger.debug("ProfilerTryCatchMethodVisitor.visitMethod(): tag=" + tag);
        super.visitCode();

        if (profiling()) {
            maintainer.addRecorder(tag, profilerAV.getProfilerParams());

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
            mv.visitLabel(startFinally);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        Logger.debug("ProfilerTryCatchMethodVisitor.visitMaxs(" + maxStack + ", " + maxLocals + "): tag=" + tag);

        if (profiling()) {
            Label endFinally = new Label();
            mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
            mv.visitLabel(endFinally);
            onFinally(ATHROW);
            mv.visitInsn(ATHROW);
        }
        mv.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void onMethodExit(int opcode) {
        Logger.debug("ProfilerTryCatchMethodVisitor.onMethodExit(" + opcode + "): tag=" + tag);

        if (opcode != ATHROW && profiling()) {
            onFinally(opcode);
        }
    }

    private void onFinally(int opcode) {
        mv.visitVarInsn(LLOAD, startTimeIdentifier);
        mv.visitLdcInsn(tag);
        mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JLjava/lang/String;)V", false);
    }

    @Override
    public void visitEnd() {
        Logger.debug("ProfilerTryCatchMethodVisitor.visitEnd(): tag=" + tag);
        super.visitEnd();
    }
}
