package cn.myperf4j.asm.aop;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class AOPClassAdapter extends ClassVisitor implements Opcodes {

    private Class<?> analysisClass;

    private boolean isInterface;

    public AOPClassAdapter(final ClassVisitor cv, Class<?> clazz) {
        super(ASM5, cv);
        this.analysisClass = clazz;
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
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);//先得到原始的方法
        if (isInterface || mv == null || "<init>".equals(name) || "<clinit>".equals(name)) {
            return mv;
        }

        return new AOPMethodVisitor(access, name, desc, mv, analysisClass.getSimpleName()); //访问需要修改的方法
    }
}
