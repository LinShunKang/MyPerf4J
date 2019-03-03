package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.base.util.TypeDescUtils;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.core.recorder.AbstractRecorderMaintainer;
import cn.myperf4j.core.MethodTagMaintainer;
import cn.myperf4j.base.config.ProfilingConfig;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by LinShunkang on 2018/4/15
 */
public class ProfilingMethodVisitor extends AdviceAdapter {

    private static final String PROFILING_ASPECT_INNER_NAME = Type.getInternalName(ProfilingAspect.class);

    private static final MethodTagMaintainer methodTagMaintainer = MethodTagMaintainer.getInstance();

    private AbstractRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();

    private ProfilingConfig profilingConfig = ProfilingConfig.getInstance();

    private String innerClassName;

    private String methodName;

    private int methodTagId;

    private int startTimeIdentifier;

    public ProfilingMethodVisitor(int access,
                                  String name,
                                  String desc,
                                  MethodVisitor mv,
                                  String innerClassName) {
        super(ASM5, mv, access, name, desc);
        this.methodName = name;
        this.methodTagId = methodTagMaintainer.addMethodTag(getMethodTag(innerClassName, name, desc));
        this.innerClassName = innerClassName;
    }

    private MethodTag getMethodTag(String innerClassName, String methodName, String desc) {
        int idx = innerClassName.replace('.', '/').lastIndexOf('/');
        String simpleClassName = innerClassName.substring(idx + 1);
        String methodParamDesc = profilingConfig.isShowMethodParams() ? "(" + TypeDescUtils.getMethodParamsDesc(desc) + ")" : "";
        return MethodTag.getGeneralInstance(simpleClassName, methodName, methodParamDesc);
    }

    @Override
    protected void onMethodEnter() {
        if (profiling()) {
            maintainer.addRecorder(methodTagId, profilingConfig.getProfilingParam(innerClassName + "/" + methodName));

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            startTimeIdentifier = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (profiling() && ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW)) {
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(methodTagId);
            mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JI)V", false);
        }
    }

    private boolean profiling() {
        return methodTagId >= 0;
    }
}
