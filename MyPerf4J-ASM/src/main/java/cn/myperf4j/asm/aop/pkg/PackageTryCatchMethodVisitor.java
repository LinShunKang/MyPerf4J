package cn.myperf4j.asm.aop.pkg;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.asm.aop.ProfilingAspect;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.TagMaintainer;
import cn.myperf4j.core.config.ProfilerParams;
import cn.myperf4j.core.util.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class PackageTryCatchMethodVisitor extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private AbstractRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();

    private String tag;

    private int tagId;

    private int startTimeIdentifier;

    private Label startFinally = new Label();


    public PackageTryCatchMethodVisitor(int access,
                                        String name,
                                        String desc,
                                        MethodVisitor mv,
                                        String className) {
        super(ASM5, mv, access, name, desc);
        this.tag = className + "." + name;
        this.tagId = TagMaintainer.getInstance().addTag(tag);
    }

    /**
     * 此方法在访问方法的头部时被访问到，仅被访问一次
     */
    @Override
    public void visitCode() {
        super.visitCode();

        if (profiling()) {
            Logger.debug("PackageTryCatchMethodVisitor.visitMethod(): tag=" + tag);

            maintainer.addRecorder(tagId, tag, ProfilerParams.of(false, 300, 10));

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
            mv.visitLabel(startFinally);
        }
    }

    private boolean profiling() {
        return tagId >= 0;
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (profiling()) {
            Logger.debug("PackageTryCatchMethodVisitor.visitMaxs(" + maxStack + ", " + maxLocals + "): tag=" + tag);
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
        if (profiling()) {
            Logger.debug("PackageTryCatchMethodVisitor.onMethodExit(" + opcode + "): tag=" + tag);
            if (opcode != ATHROW) {
                onFinally(opcode);
            }
        }
    }

    private void onFinally(int opcode) {
        mv.visitVarInsn(LLOAD, startTimeIdentifier);
        mv.visitLdcInsn(tagId);
        mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JI)V", false);
    }

}
