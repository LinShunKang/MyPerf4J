package cn.myperf4j.asm.aop;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Type.LONG_TYPE;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class ProfilingDynamicMethodVisitor extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private final String innerClassName;

    private final int startTimeIdentifier;

    private final int throwableIdentifier;

    private final Label tryStart = new Label();

    private final Label tryEnd = new Label();

    private final Label catchStart = new Label();

    private final Label catchEnd = new Label();

    public ProfilingDynamicMethodVisitor(int access,
                                         String name,
                                         String desc,
                                         MethodVisitor mv,
                                         String innerClassName) {
        super(ASM9, mv, access, name, desc);
        this.innerClassName = innerClassName;
        this.startTimeIdentifier = newLocal(LONG_TYPE);
        this.throwableIdentifier = newLocal(Type.getType(Throwable.class));
    }

    @Override
    protected void onMethodEnter() {
        mv.visitTryCatchBlock(tryStart, tryEnd, catchStart, null);
        mv.visitTryCatchBlock(catchStart, catchEnd, catchStart, null);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        mv.visitVarInsn(LSTORE, startTimeIdentifier);
        mv.visitLabel(tryStart);
    }

    @Override
    protected void onMethodExit(int opcode) {
        if ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW) {
            mv.visitLabel(tryEnd);
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling",
                    "(JLjava/lang/reflect/Method;)V", false);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitLabel(catchStart);
        mv.visitFrame(F_FULL, 3, new Object[]{innerClassName, LONG, LONG}, 1, new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, throwableIdentifier);

        mv.visitLabel(catchEnd);
        mv.visitVarInsn(LLOAD, startTimeIdentifier);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling",
                "(JLjava/lang/reflect/Method;)V", false);
        mv.visitVarInsn(ALOAD, throwableIdentifier);
        mv.visitInsn(ATHROW);

        super.visitMaxs(maxStack, maxLocals);
    }
}
