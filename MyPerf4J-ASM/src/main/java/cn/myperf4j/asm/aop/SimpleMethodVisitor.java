package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.ProfilerParams;
import cn.myperf4j.core.annotation.NonProfiler;
import cn.myperf4j.core.annotation.Profiler;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class SimpleMethodVisitor extends LocalVariablesSorter {

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

    public SimpleMethodVisitor(int access,
                               String name,
                               String desc,
                               MethodVisitor mv,
                               String className,
                               ProfilerParams classProfilerParams) {
        super(ASM5, access, desc, mv);
        this.tag = className + "." + name;
        this.classProfilerParams = classProfilerParams;
        this.hasProfiler = classProfilerParams.hasProfiler();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
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
        super.visitCode();

        if (profiling()) {
            maintainer.addRecorder(tag, profilerAV.getProfilerParams());

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
        }
    }

    /**
     * 此方法可以获取方法中每一条指令的操作类型，被访问多次
     * 如应在方法结尾处添加新指令，则应判断opcode的类型
     */
    @Override
    public void visitInsn(int opcode) {
        if (profiling() && ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW)) {
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(tag);
            mv.visitMethodInsn(INVOKESTATIC, ProfilerAspect.class.getName().replace(".", "/"), "profiling", "(JLjava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }
}
