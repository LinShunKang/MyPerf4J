package MyPerf4J;

import cn.myperf4j.asm.ASMBootstrap;
import cn.myperf4j.asm.aop.ProfilingTransformer;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues.Metrics;
import cn.myperf4j.base.util.ThreadUtils;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class PreMainTest {

    @Test
    public void test() {
        test(Metrics.EXPORTER_LOG_STDOUT);
//        test(PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB);
    }

    private void test(String exporter) {
        prepare(exporter);
        if (ASMBootstrap.getInstance().initial()) {
            MyClassLoader loader = new MyClassLoader();
            Class<?> aClass = loader.findClass("MyPerf4J.ClassToTest");
            try {
                Object obj = aClass.newInstance();
                Method method = aClass.getMethod("getStr");
                for (int i = 0; i < 100; ++i) {
                    method.invoke(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ThreadUtils.sleepQuietly(2, TimeUnit.SECONDS);
        }
    }

    private void prepare(String exporter) {
        String propertiesFile = "/tmp/MyPerf4J.properties";
        System.setProperty(PropertyKeys.PRO_FILE_NAME, propertiesFile);
        AutoRollingFileWriter writer = new MinutelyRollingFileWriter(propertiesFile, 1);
        writer.write("app_name=MyPerf4JTest\n");
        writer.write("metrics.exporter=" + exporter + "\n");
        writer.write("filter.packages.include=MyPerf4J\n");
        writer.write("metrics.time_slice.method=1000\n");
        writer.write("metrics.time_slice.jvm=1000\n");
        writer.closeFile(true);

        new File(propertiesFile).deleteOnExit();
    }

    public static class MyClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) {
            ProfilingTransformer transformer = new ProfilingTransformer();
            Class<?> targetClass = ClassToTest.class;
            try {
                byte[] transformBytes = transformer.transform(PreMainTest.class.getClassLoader(), targetClass.getName(), targetClass, null, ClassFileUtils.getClassFileContent(targetClass.getName()));
                return defineClass(name, transformBytes, 0, transformBytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
