package MyPerf4J.test1;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by LinShunkang on 2018/4/23
 */
public class FinallyAdapter extends AdviceAdapter {

    private String name;

    private Label startFinally = new Label();

    public FinallyAdapter(MethodVisitor mv, int acc, String name, String desc) {
        super(Opcodes.ASM5, mv, acc, name, desc);
        this.name = name;
    }

    public void visitCode() {
        super.visitCode();
        mv.visitLabel(startFinally);
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        Label endFinally = new Label();
        mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null);
        mv.visitLabel(endFinally);
        onFinally(ATHROW);
        mv.visitInsn(ATHROW);
        mv.visitMaxs(maxStack, maxLocals);
    }

    protected void onMethodExit(int opcode) {
        if (opcode != ATHROW) {
            onFinally(opcode);
        }
    }

    private void onFinally(int opcode) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Exiting " + name);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
