package cn.myperf4j.base;

/**
 * Created by LinShunkang on 2018/7/13
 */
public class MethodTag {

    private final String className;

    private final String methodName;

    private final String methodParamDesc;

    private final String description;

    private MethodTag(String className, String methodName, String methodParamDesc) {
        this.className = className;
        this.methodName = methodName;
        this.methodParamDesc = methodParamDesc;
        this.description = className + "." + methodName + methodParamDesc;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodParamDesc() {
        return methodParamDesc;
    }

    public String getSimpleDesc() {
        return description;
    }

    @Override
    public String toString() {
        return "MethodTag{" +
                "methodName='" + methodName + '\'' +
                ", className='" + className + '\'' +
                '}';
    }

    public static MethodTag getInstance(String className, String methodName, String methodParamDesc) {
        return new MethodTag(className, methodName, methodParamDesc);
    }
}
