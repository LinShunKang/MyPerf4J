package cn.myperf4j.base;

/**
 * Created by LinShunkang on 2018/7/13
 */
public class MethodTag {

    private static final String TYPE_GENERAL = "General";

    private static final String TYPE_DYNAMIC_PROXY = "DynamicProxy";

    private final String className;

    private final String methodName;

    private final String methodParamDesc;

    private final String description;

    private final String type;

    private MethodTag(String className, String methodName, String methodParamDesc, String type) {
        this.className = className;
        this.methodName = methodName;
        this.methodParamDesc = methodParamDesc;
        this.description = className + "." + methodName + methodParamDesc;
        this.type = type;
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

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MethodTag{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodParamDesc='" + methodParamDesc + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public static MethodTag getGeneralInstance(String className, String methodName, String methodParamDesc) {
        return new MethodTag(className, methodName, methodParamDesc, TYPE_GENERAL);
    }

    public static MethodTag getDynamicProxyInstance(String className, String methodName, String methodParamDesc) {
        return new MethodTag(className, methodName, methodParamDesc, TYPE_DYNAMIC_PROXY);
    }
}
