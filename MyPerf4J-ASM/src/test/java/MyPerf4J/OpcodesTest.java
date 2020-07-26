package MyPerf4J;

import org.junit.Test;

import static org.objectweb.asm.Opcodes.*;

public class OpcodesTest {

    @Test
    public void test() {
        int num = 4161;

        if ((num & ACC_PUBLIC) != 0) {
            System.out.println("ACC_PUBLIC");
        }
        if ((num & ACC_PRIVATE) != 0) {
            System.out.println("ACC_PRIVATE");
        }
        if ((num & ACC_PROTECTED) != 0) {
            System.out.println("ACC_PROTECTED");
        }
        if ((num & ACC_FINAL) != 0) {
            System.out.println("ACC_FINAL");
        }
        if ((num & ACC_SUPER) != 0) {
            System.out.println("ACC_SUPER");
        }
        if ((num & ACC_SYNCHRONIZED) != 0) {
            System.out.println("ACC_SYNCHRONIZED");
        }
        if ((num & ACC_TRANSITIVE) != 0) {
            System.out.println("ACC_TRANSITIVE");
        }
        if ((num & ACC_VOLATILE) != 0) {
            System.out.println("ACC_VOLATILE");
        }
        if ((num & ACC_BRIDGE) != 0) {
            System.out.println("ACC_BRIDGE");
        }
        if ((num & ACC_STATIC_PHASE) != 0) {
            System.out.println("ACC_STATIC_PHASE");
        }
        if ((num & ACC_VARARGS) != 0) {
            System.out.println("ACC_VARARGS");
        }
        if ((num & ACC_TRANSIENT) != 0) {
            System.out.println("ACC_TRANSIENT");
        }
        if ((num & ACC_NATIVE) != 0) {
            System.out.println("ACC_NATIVE");
        }
        if ((num & ACC_INTERFACE) != 0) {
            System.out.println("ACC_INTERFACE");
        }
        if ((num & ACC_ABSTRACT) != 0) {
            System.out.println("ACC_ABSTRACT");
        }
        if ((num & ACC_STRICT) != 0) {
            System.out.println("ACC_STRICT");
        }
        if ((num & ACC_SYNTHETIC) != 0) {
            System.out.println("ACC_SYNTHETIC");
        }
        if ((num & ACC_ANNOTATION) != 0) {
            System.out.println("ACC_ANNOTATION");
        }
        if ((num & ACC_MANDATED) != 0) {
            System.out.println("ACC_MANDATED");
        }
        if ((num & ACC_MODULE) != 0) {
            System.out.println("ACC_MODULE");
        }
        if ((num & ACC_DEPRECATED) != 0) {
            System.out.println("ACC_DEPRECATED");
        }
    }
}
