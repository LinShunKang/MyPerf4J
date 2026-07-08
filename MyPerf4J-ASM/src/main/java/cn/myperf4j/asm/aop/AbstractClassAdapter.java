package cn.myperf4j.asm.aop;

import cn.myperf4j.base.config.LevelMappingFilter;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.ProfilingFilter;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.util.TypeDescUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ASM9;

/**
 * Created by LinShunkang on 2026/07/05
 */
public abstract class AbstractClassAdapter extends ClassVisitor {

    protected final String innerClassName;

    protected final String fullClassName;

    protected final String simpleClassName;

    protected final String classLevel;

    protected final Set<String> fieldMethods;

    protected boolean isInterface;

    public AbstractClassAdapter(ClassVisitor cv, String innerClassName) {
        super(ASM9, cv);
        this.innerClassName = innerClassName;
        this.fullClassName = innerClassName.replace('/', '.');
        this.simpleClassName = TypeDescUtils.getSimpleClassName(innerClassName);
        this.classLevel = LevelMappingFilter.getClassLevel(simpleClassName);
        this.fieldMethods = new HashSet<>();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        Logger.debug("AbstractClassAdapter.visit(" + version + ", " + access + ", " + name + ", "
                + signature + ", " + superName + ", " + Arrays.toString(interfaces) + ")");

        super.visit(version, access, name, signature, superName, interfaces);
        this.isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        final String upFieldName = name.substring(0, 1).toUpperCase() + name.substring(1);
        fieldMethods.add("get" + upFieldName);
        fieldMethods.add("set" + upFieldName);
        fieldMethods.add("is" + upFieldName);
        return super.visitField(access, name, desc, signature, value);
    }

    protected boolean isNeedVisit(int access, String name) {
        //不对私有方法进行注入
        if ((access & ACC_PRIVATE) != 0 && ProfilingConfig.filterConfig().excludePrivateMethod()) {
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
        return !fieldMethods.contains(name) && !ProfilingFilter.isNotNeedInjectMethod(name);
    }
}
