package cn.myperf4j.asm.aop;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Type.LONG_TYPE;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class ProfilingDynamicMethodVisitor extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private int startTimeIdentifier;

    public ProfilingDynamicMethodVisitor(int access,
                                         String name,
                                         String desc,
                                         MethodVisitor mv) {
        super(ASM9, mv, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        startTimeIdentifier = newLocal(LONG_TYPE);
        mv.visitVarInsn(LSTORE, startTimeIdentifier);
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (IRETURN <= opcode && opcode <= RETURN || opcode == ATHROW) {
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling",
                    "(JLjava/lang/reflect/Method;)V", false);
        }
    }
}
