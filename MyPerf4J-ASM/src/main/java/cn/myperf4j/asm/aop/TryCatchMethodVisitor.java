package cn.myperf4j.asm.aop;

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
public class TryCatchMethodVisitor extends AdviceAdapter {

    private static final String PROFILER_I_NAME = Type.getDescriptor(Profiler.class);

    private boolean needProfiling = false;

    private String tag;

    private int startTimeIdentifier;

    private Label startFinally = new Label();//


    public TryCatchMethodVisitor(int access,
                                 String name,
                                 String desc,
                                 MethodVisitor mv,
                                 String className) {
        super(ASM5, mv, access, name, desc);
        this.tag = className + "." + name;
    }

    /**
     * 此方法在访问方法的头部时被访问到，仅被访问一次
     */
    @Override
    public void visitCode() {
        Logger.info("TryCatchMethodVisitor.visitMethod(): tag=" + tag);

        super.visitCode();

        if (needProfiling) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);

            mv.visitLabel(startFinally);//
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (needProfiling) {
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
        Logger.info("TryCatchMethodVisitor.onMethodExit(" + opcode + "): tag=" + tag);
        if (opcode != ATHROW && needProfiling) {
            onFinally(opcode);
        }
    }

    private void onFinally(int opcode) {
        mv.visitVarInsn(LLOAD, startTimeIdentifier);
        mv.visitLdcInsn(tag);
        mv.visitMethodInsn(INVOKESTATIC, ProfilerAspect.class.getName().replace(".", "/"), "profiling", "(JLjava/lang/String;)V", false);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        needProfiling = PROFILER_I_NAME.equals(desc);
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitEnd() {
        Logger.info("TryCatchMethodVisitor.visitEnd(): tag=" + tag);
        super.visitEnd();
    }
}
