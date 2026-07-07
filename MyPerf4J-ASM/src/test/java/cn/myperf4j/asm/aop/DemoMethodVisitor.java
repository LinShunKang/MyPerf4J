package cn.myperf4j.asm.aop;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Type.LONG_TYPE;

/**
 * Created by LinShunkang on 2026/07/06
 */
public class DemoMethodVisitor extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private final int methodTagId;

    private final Label startFinally = new Label();

    private final Label catchFinally = new Label();

    private int startTimeIdentifier;

    protected DemoMethodVisitor(int access, String name, String desc, MethodVisitor mv) {
        super(ASM9, mv, access, name, desc);
        this.methodTagId = 100;
    }

    @Override
    protected void onMethodEnter() {
        if (profiling()) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (profiling()) {
            mv.visitLabel(startFinally);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (profiling() && opcode != ATHROW) {
            profilingMethod();
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (profiling()) {
            Label endFinally = new Label();
            mv.visitTryCatchBlock(startFinally, endFinally, catchFinally, null);
            mv.visitLabel(endFinally);
            mv.visitLabel(catchFinally);
            profilingMethod();
            mv.visitInsn(ATHROW);
        }
        super.visitMaxs(maxStack, maxLocals);
    }

    private void profilingMethod() {
        mv.visitVarInsn(LLOAD, startTimeIdentifier);
        push(methodTagId);
        mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JI)V", false);
    }

    private boolean profiling() {
        return methodTagId >= 0;
    }
}
