package cn.myperf4j.asm.aop;

import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.TypeDescUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class ProfilingClassAdapter extends AbstractClassAdapter {

    private static final String JAVA_INVOCATION_HANDLER = "java/lang/reflect/InvocationHandler";

    private boolean isInvocationHandler;

    public ProfilingClassAdapter(final ClassVisitor cv, String innerClassName) {
        super(cv, innerClassName);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.isInvocationHandler = isInvocationHandler(interfaces);
    }

    private boolean isInvocationHandler(String[] interfaces) {
        if (interfaces == null) {
            return false;
        }

        for (String inf : interfaces) {
            if (JAVA_INVOCATION_HANDLER.equals(inf)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String desc,
                                     String signature,
                                     String[] exceptions) {
        if (isInterface || !isNeedVisit(access, name)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        final String classMethodName = simpleClassName + "." + name;
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        final String desc4Human = TypeDescUtils.getMethodParamsDesc(desc);
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName + desc4Human)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        final MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null) {
            return null;
        }
        Logger.debug("ProfilingClassAdapter.visitMethod(" + access + ", " + name + ", " + desc + ", "
                + signature + ", " + Arrays.toString(exceptions) + "), innerClassName=" + innerClassName);

        if (isInvocationHandler && isInvokeMethod(name, desc)) {
            return new ProfilingDynamicMethodVisitor(access, name, desc, mv);
        } else {
            return new ProfilingMethodVisitor(access, name, desc, mv, innerClassName, fullClassName, simpleClassName,
                    classLevel, desc4Human);
        }
    }

    private boolean isInvokeMethod(String methodName, String methodDesc) {
        return methodName.equals("invoke")
                && methodDesc.equals(
                "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
    }
}
