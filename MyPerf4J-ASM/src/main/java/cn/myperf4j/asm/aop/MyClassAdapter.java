package cn.myperf4j.asm.aop;

import cn.myperf4j.core.util.Logger;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class MyClassAdapter extends ClassVisitor implements Opcodes {

    private String targetClassName;

    private boolean addTryCatch;//是否把方法体包进try-finally块；也就是当方法内抛异常的时候是否还记录响应时间；

    private boolean isInterface;

    public MyClassAdapter(final ClassVisitor cv, String targetClassName, boolean addTryCatch) {
        super(ASM5, cv);
        int idx = targetClassName.replace('/', '.').lastIndexOf('.');
        this.targetClassName = targetClassName.substring(idx + 1, targetClassName.length());
        this.addTryCatch = addTryCatch;
    }

    public MyClassAdapter(final ClassVisitor cv, String targetClassName) {
        this(cv, targetClassName, false);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        Logger.info("MyClassAdapter.visitMethod(" + access + ", " + name + ", " + desc + ", " + signature + ", "  + Arrays.toString(exceptions) + ")");

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (isInterface || mv == null || "<init>".equals(name) || "<clinit>".equals(name)) {
            return mv;
        }

        if (addTryCatch) {
            return new TryCatchMethodVisitor(access, name, desc, mv, targetClassName);
        } else {
            return new SimpleMethodVisitor(access, name, desc, mv, targetClassName);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }
}
