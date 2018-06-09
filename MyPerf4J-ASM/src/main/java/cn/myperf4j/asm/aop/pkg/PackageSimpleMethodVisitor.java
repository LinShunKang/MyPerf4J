package cn.myperf4j.asm.aop.pkg;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.asm.aop.ProfilingAspect;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.TagMaintainer;
import cn.myperf4j.core.config.ProfilingConfig;
import cn.myperf4j.core.config.ProfilingParams;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class PackageSimpleMethodVisitor extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private AbstractRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();

    private ProfilingConfig profilingConfig = ProfilingConfig.getInstance();

    private String innerClassName;

    private String methodName;

    private String tag;

    private int tagId;

    private int startTimeIdentifier;

    public PackageSimpleMethodVisitor(int access,
                                      String name,
                                      String desc,
                                      MethodVisitor mv,
                                      String innerClassName) {
        super(ASM5, mv, access, name, desc);
        this.methodName = name;
        this.tag = getTag(innerClassName, name);
        this.tagId = TagMaintainer.getInstance().addTag(tag);
        this.innerClassName = innerClassName;
    }

    private String getTag(String innerClassName, String methodName) {
        int idx = innerClassName.replace('.', '/').lastIndexOf('/');
        String simpleClassName = innerClassName.substring(idx + 1, innerClassName.length());
        return simpleClassName + "." + methodName;
    }

    @Override
    protected void onMethodEnter() {
        if (profiling()) {
            maintainer.addRecorder(tagId, tag, profilingConfig.getProfilingParam(innerClassName + "/" + methodName));

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (profiling() && ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW)) {
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(tagId);
            mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JI)V", false);
        }
    }

    private boolean profiling() {
        return tagId >= 0;
    }
}
