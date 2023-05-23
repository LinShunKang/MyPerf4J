package MyPerf4J.bytecode;

import cn.myperf4j.base.util.io.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;

public class ForCompareDump implements Opcodes {

    public static void main(String[] args) throws Exception {
        final byte[] bytes = dump();
        FileUtils.writeToFile(bytes, new File("MyPerf4J-ASM/target/test-classes/MyPerf4J/bytecode/ForCompare2.class"));
    }

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        classWriter.visit(V1_7, ACC_PUBLIC | ACC_SUPER, "MyPerf4J/bytecode/ForCompare2", null, "java/lang/Object", null);

        classWriter.visitSource("ForCompare.java", null);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(8, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "LMyPerf4J/bytecode/ForCompare;", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getId0", "(J)J", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(11, label0);
            methodVisitor.visitVarInsn(LLOAD, 1);
            methodVisitor.visitLdcInsn(new Long(8L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitInsn(LRETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "LMyPerf4J/bytecode/ForCompare;", null, label0, label1, 0);
            methodVisitor.visitLocalVariable("id", "J", null, label0, label1, 1);
            methodVisitor.visitMaxs(4, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getId2", "(J)J", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, null);
            Label label3 = new Label();
            methodVisitor.visitTryCatchBlock(label2, label3, label2, null);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(22, label4);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            methodVisitor.visitVarInsn(LSTORE, 3);
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(24, label0);
            methodVisitor.visitVarInsn(LLOAD, 1);
            methodVisitor.visitLdcInsn(new Long(8L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(LSTORE, 5);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(26, label1);
            methodVisitor.visitVarInsn(LLOAD, 3);
            methodVisitor.visitIntInsn(BIPUSH, 7);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "cn/myperf4j/asm/aop/ProfilingAspect", "profiling", "(JI)V", false);
            methodVisitor.visitVarInsn(LLOAD, 5);
            methodVisitor.visitInsn(LRETURN);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitFrame(Opcodes.F_FULL, 3, new Object[]{"MyPerf4J/bytecode/ForCompare", Opcodes.LONG, Opcodes.LONG}, 1, new Object[]{"java/lang/Throwable"});
            methodVisitor.visitVarInsn(ASTORE, 7);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitVarInsn(LLOAD, 3);
            methodVisitor.visitIntInsn(BIPUSH, 7);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "cn/myperf4j/asm/aop/ProfilingAspect", "profiling", "(JI)V", false);
            methodVisitor.visitVarInsn(ALOAD, 7);
            methodVisitor.visitInsn(ATHROW);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLocalVariable("this", "LMyPerf4J/bytecode/ForCompare;", null, label4, label5, 0);
            methodVisitor.visitLocalVariable("id", "J", null, label4, label5, 1);
            methodVisitor.visitLocalVariable("start", "J", null, label0, label5, 3);
            methodVisitor.visitMaxs(4, 8);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}