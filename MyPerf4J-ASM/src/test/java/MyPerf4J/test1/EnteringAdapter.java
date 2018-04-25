package MyPerf4J.test1;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by LinShunkang on 2018/4/23
 */
public class EnteringAdapter extends AdviceAdapter {

    private String name;
    private int timeVar;
    private Label timeVarStart = new Label();
    private Label timeVarEnd = new Label();

    public EnteringAdapter(MethodVisitor mv, int acc, String name, String desc) {
        super(Opcodes.ASM5, mv, acc, name, desc);
        this.name = name;
    }

    protected void onMethodEnter() {
        visitLabel(timeVarStart);
        int timeVar = newLocal(Type.getType("J"));
        visitLocalVariable("timeVar", "J", null, timeVarStart, timeVarEnd, timeVar);
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        super.visitLdcInsn("Entering " + name);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public void visitMaxs(int stack, int locals) {
        visitLabel(timeVarEnd);
        super.visitMaxs(stack, locals);
    }

}
