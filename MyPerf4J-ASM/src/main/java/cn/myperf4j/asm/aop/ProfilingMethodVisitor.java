package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.RecorderConfig;
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

    private final AbstractRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();

    private final MetricsConfig metricsConf = ProfilingConfig.metricsConfig();

    private final RecorderConfig recorderConf = ProfilingConfig.recorderConfig();

    private final String innerClassName;

    private final String methodName;

    private final int methodTagId;

    private int startTimeIdentifier;

    public ProfilingMethodVisitor(int access,
                                  String name,
                                  String desc,
                                  MethodVisitor mv,
                                  String innerClassName,
                                  String fullClassName,
                                  String simpleClassName,
                                  String classLevel,
                                  String humanMethodDesc) {
        super(ASM8, mv, access, name, desc);
        this.methodName = name;
        this.methodTagId = methodTagMaintainer.addMethodTag(
                getMethodTag(fullClassName, simpleClassName, classLevel, name, humanMethodDesc));
        this.innerClassName = innerClassName;
    }

    private MethodTag getMethodTag(String fullClassName,
                                   String simpleClassName,
                                   String classLevel,
                                   String methodName,
                                   String humanMethodDesc) {
        String methodParamDesc = metricsConf.showMethodParams() ? humanMethodDesc : "";
        return MethodTag.getGeneralInstance(fullClassName, simpleClassName, classLevel, methodName, methodParamDesc);
    }

    @Override
    protected void onMethodEnter() {
        if (profiling()) {
            maintainer.addRecorder(methodTagId, recorderConf.getProfilingParam(innerClassName + "/" + methodName));

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
