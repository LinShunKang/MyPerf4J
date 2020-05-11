package cn.myperf4j.asm.aop;

import cn.myperf4j.base.config.LevelMappingFilter;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.TypeDescUtils;
import org.objectweb.asm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class ProfilingClassAdapter extends ClassVisitor {

    private final String innerClassName;

    private final String fullClassName;

    private final String simpleClassName;

    private final String classLevel;

    private boolean isInterface;

    private boolean isInvocationHandler;

    private final List<String> fieldNameList = new ArrayList<>();

    public ProfilingClassAdapter(final ClassVisitor cv, String innerClassName) {
        super(ASM5, cv);
        this.innerClassName = innerClassName;
        this.fullClassName = innerClassName.replace('/', '.');
        this.simpleClassName = TypeDescUtils.getSimpleClassName(innerClassName);
        this.classLevel = LevelMappingFilter.getClassLevel(simpleClassName);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Logger.debug("ProfilingClassAdapter.visit(" + version + ", " + access + ", " + name + ", " + signature + ", " + superName + ", " + Arrays.toString(interfaces) + ")");

        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
        this.isInvocationHandler = isInvocationHandler(interfaces);
    }

    private boolean isInvocationHandler(String[] interfaces) {
        if (interfaces == null || interfaces.length <= 0) {
            return false;
        }

        for (int i = 0; i < interfaces.length; ++i) {
            if ("java/lang/reflect/InvocationHandler".equals(interfaces[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        String upFieldName = name.substring(0, 1).toUpperCase() + name.substring(1);
        fieldNameList.add("get" + upFieldName);
        fieldNameList.add("set" + upFieldName);
        fieldNameList.add("is" + upFieldName);

        return super.visitField(access, name, desc, signature, value);
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

        String classMethodName = simpleClassName + "." + name;
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        String desc4Human = TypeDescUtils.getMethodParamsDesc(desc);
        classMethodName = classMethodName + desc4Human;
        if (ProfilingFilter.isNotNeedInjectMethod(classMethodName)) {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if (mv == null) {
            return null;
        }
        Logger.debug("ProfilingClassAdapter.visitMethod(" + access + ", " + name + ", " + desc + ", " + signature + ", " + Arrays.toString(exceptions) + "), innerClassName=" + innerClassName);

        if (isInvocationHandler && isInvokeMethod(name, desc)) {
            return new ProfilingDynamicMethodVisitor(access, name, desc, mv);
        } else {
            return new ProfilingMethodVisitor(access, name, desc, mv, innerClassName, fullClassName, simpleClassName, classLevel, desc4Human);
        }
    }

    private boolean isNeedVisit(int access, String name) {
        //不对私有方法进行注入
        if ((access & ACC_PRIVATE) != 0 && ProfilingConfig.getInstance().isExcludePrivateMethod()) {
            return false;
        }

        //不对抽象方法、native方法、桥接方法、合成方法进行注入
        if ((access & ACC_ABSTRACT) != 0
                || (access & ACC_NATIVE) != 0
                || (access & ACC_BRIDGE) != 0
                || (access & ACC_SYNTHETIC) != 0) {
            return false;
        }

        if ("<init>".equals(name) || "<clinit>".equals(name)) {
            return false;
        }

        if (fieldNameList.contains(name) || ProfilingFilter.isNotNeedInjectMethod(name)) {
            return false;
        }

        return true;
    }

    private boolean isInvokeMethod(String methodName, String methodDesc) {
        return methodName.equals("invoke") && methodDesc.equals("(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;");
    }
}
