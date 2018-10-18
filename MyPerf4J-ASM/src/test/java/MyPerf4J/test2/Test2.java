package MyPerf4J.test2;

import cn.myperf4j.asm.ASMRecorderMaintainer;
import cn.myperf4j.asm.aop.ProfilingClassAdapter;
import cn.myperf4j.base.metric.processor.stdout.StdoutMethodMetricsProcessor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by LinShunkang on 2018/4/23
 */
public class Test2 {

    public static void main(String[] args) throws IOException {
        ASMRecorderMaintainer instance = ASMRecorderMaintainer.getInstance();
        instance.initial(new StdoutMethodMetricsProcessor(), true, 1);
        test2();
//        runNewFoo2();
    }

    private static void test2() throws IOException {
        ClassReader cr = new ClassReader(Foo2.class.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ProfilingClassAdapter(cw, Foo2.class.getName());
        cr.accept(cv, ClassReader.SKIP_FRAMES);

        byte[] bytes = cw.toByteArray();
        rewriteClass(bytes);
    }

    private static void rewriteClass(byte[] toByte) throws IOException {
        File tofile = new File("/Users/thinker/WorkSpace/personal/MyPerf4J/MyPerf4J-ASM/target/test-classes/MyPerf4J/test2/Foo2.class");
        FileOutputStream fout = new FileOutputStream(tofile);
        fout.write(toByte);
        fout.close();
    }

    private static void runNewFoo2() {
        Foo2.test1();
        Foo2.test2();
        Foo2.test3();
        Foo2.test4();

        System.out.println(Foo2.test5(3));
    }
}
