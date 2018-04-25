package MyPerf4J.test1;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by LinShunkang on 2018/4/23
 */
public class Test {

    public static void main(String[] args) throws IOException {
        test1();
//        runNewFoo();
    }

    private static void test1() throws IOException {
        ClassReader cr = new ClassReader(Foo.class.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        MyClassAdapter cv = new MyClassAdapter(cw, Foo.class);
        cr.accept(cv, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        byte[] bytes = cw.toByteArray();
        rewriteClass(bytes);
    }

    private static void rewriteClass(byte[] toByte) throws IOException {
        File tofile = new File("/Users/thinker/WorkSpace/personal/MyPerf4J/MyPerf4J-ASM/target/test-classes/MyPerf4J/Foo.class");
        FileOutputStream fout = new FileOutputStream(tofile);
        fout.write(toByte);
        fout.close();
    }

    private static void runNewFoo() {
        Foo.test1();
    }
}
