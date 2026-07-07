package cn.myperf4j.asm.aop;

import MyPerf4J.test1.Foo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;

/**
 * Created by LinShunkang on 2026/07/06
 */
public final class TransformerTest {

    private TransformerTest() {
        //empty
    }

    public static void main(String[] args) throws IOException {
        final String className = Foo.class.getName();
        final ClassReader cr = new ClassReader(className);
        final ClassWriter cw = new ClassWriter(cr, COMPUTE_FRAMES);
        cr.accept(new DemoClassAdapter(cw, className.replace('.', '/')), EXPAND_FRAMES);

        rewriteClass(cw.toByteArray());
    }

    private static void rewriteClass(byte[] toByte) throws IOException {
        File tofile = new File("/Users/linshunkang/WorkSpace/personal2/MyPerf4J/MyPerf4J-ASM/target/Foo.class");
        try (FileOutputStream os = new FileOutputStream(tofile)) {
            os.write(toByte);
        }
    }
}
