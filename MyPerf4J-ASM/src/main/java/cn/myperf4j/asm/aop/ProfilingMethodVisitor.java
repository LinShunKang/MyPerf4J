package cn.myperf4j.asm.aop;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.base.MethodTag;
import cn.myperf4j.base.config.MetricsConfig;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.config.RecorderConfig;
import cn.myperf4j.core.MethodTagMaintainer;
import cn.myperf4j.core.recorder.AbstractRecorderMaintainer;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import static org.objectweb.asm.Type.LONG_TYPE;

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

    private final int startTimeIdentifier;

    private final int throwableIdentifier;

    private final Label tryStart = new Label();

    private final Label tryEnd = new Label();

    private final Label catchStart = new Label();

    private final Label catchEnd = new Label();

    public ProfilingMethodVisitor(int access,
                                  String name,
                                  String desc,
                                  MethodVisitor mv,
                                  String innerClassName,
                                  String fullClassName,
                                  String simpleClassName,
                                  String classLevel,
                                  String humanMethodDesc) {
        super(ASM9, mv, access, name, desc);
        this.methodName = name;
        this.methodTagId = methodTagMaintainer.addMethodTag(
                getMethodTag(fullClassName, simpleClassName, classLevel, name, humanMethodDesc));
        this.innerClassName = innerClassName;
        this.startTimeIdentifier = profiling() ? newLocal(LONG_TYPE) : -1;
        this.throwableIdentifier = profiling() ? newLocal(Type.getType(Throwable.class)) : -1;
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

            mv.visitTryCatchBlock(tryStart, tryEnd, catchStart, null);
            mv.visitTryCatchBlock(catchStart, catchEnd, catchStart, null);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
            mv.visitVarInsn(LSTORE, startTimeIdentifier);
            mv.visitLabel(tryStart);
        }
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (profiling() && ((IRETURN <= opcode && opcode <= RETURN) || opcode == ATHROW)) {
            mv.visitLabel(tryEnd);
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(methodTagId);
            mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JI)V", false);
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (profiling()) {
            mv.visitLabel(catchStart);
            mv.visitFrame(F_FULL, 3, new Object[]{innerClassName, LONG, LONG}, 1, new Object[]{"java/lang/Throwable"});
            mv.visitVarInsn(ASTORE, throwableIdentifier);

            mv.visitLabel(catchEnd);
            mv.visitVarInsn(LLOAD, startTimeIdentifier);
            mv.visitLdcInsn(methodTagId);
            mv.visitMethodInsn(INVOKESTATIC, PROFILING_ASPECT_INNER_NAME, "profiling", "(JI)V", false);
            mv.visitVarInsn(ALOAD, throwableIdentifier);
            mv.visitInsn(ATHROW);
        }
        super.visitMaxs(maxStack, maxLocals);
    }

    private boolean profiling() {
        return methodTagId >= 0;
    }
}
